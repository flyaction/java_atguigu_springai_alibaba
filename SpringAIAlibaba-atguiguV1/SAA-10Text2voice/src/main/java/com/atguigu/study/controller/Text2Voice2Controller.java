package com.atguigu.study.controller;

import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisOptions;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisModel;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisResponse;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisResult;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesizer;
import com.alibaba.dashscope.common.ResultCallback;
import com.atguigu.study.untis.TimeUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * @auther zzyybs@126.com
 * @create 2025-07-29 18:35
 * @Description TODO
 */
@RestController
public class Text2Voice2Controller {

    // voice model
    public static final String BAILIAN_VOICE_MODEL = "cosyvoice-v2";
    // voice timber 音色列表：https://help.aliyun.com/zh/model-studio/cosyvoice-java-sdk#722dd7ca66a6x
    public static final String BAILIAN_VOICE_TIMBER = "longyingcui";//龙应催

//    @Value("${spring.ai.dashscope.api-key}")
//    private String apiKey;


    /**
     * http://localhost:8010/t2v/voice2
     *
     * @param msg
     * @return
     */
    @GetMapping("/t2v/voice2")
    public String voice2(@RequestParam(name = "msg", defaultValue = "温馨提醒，支付宝到账100元请注意查收") String msg) {


        String filePath = "/Users/action/Downloads/" + UUID.randomUUID() + ".mp3";

        CountDownLatch latch = new CountDownLatch(1);

        // 使用 ByteArrayOutputStream 收集所有音频数据
        ByteArrayOutputStream audioBuffer = new ByteArrayOutputStream();

        // 实现回调接口ResultCallback
        ResultCallback<SpeechSynthesisResult> callback = new ResultCallback<SpeechSynthesisResult>() {
            @Override
            public void onEvent(SpeechSynthesisResult result) {
                // System.out.println("收到消息: " + result);
                if (result.getAudioFrame() != null) {
                    // 此处实现保存音频数据到本地的逻辑
                    try {
                        // 将音频数据追加到缓冲区
                        audioBuffer.write(result.getAudioFrame().array());
                        System.out.println(TimeUtils.getTimestamp() + " 收到音频数据，大小: " + result.getAudioFrame().array().length);
                    } catch (Exception e) {
                        System.out.println("写入音频数据失败: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onComplete() {
                System.out.println(TimeUtils.getTimestamp() + " 收到Complete，语音合成结束");
                // 在完成时将所有音频数据写入文件
                try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                    audioBuffer.writeTo(fileOutputStream);
                    System.out.println("音频文件已保存，总大小: " + audioBuffer.size());
                } catch (Exception e) {
                    System.out.println("保存音频文件失败: " + e.getMessage());
                }
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                System.out.println("出现异常：" + e.toString());
                latch.countDown();
            }
        };

        // 请求参数
        SpeechSynthesisParam param =
                SpeechSynthesisParam.builder()
                        // 若没有将API Key配置到环境变量中，需将下面这行代码注释放开，并将your-api-key替换为自己的API Key
                        //.apiKey("your-api-key")
                        .model(BAILIAN_VOICE_MODEL) // 模型
                        .voice(BAILIAN_VOICE_TIMBER) // 音色
                        .build();
        // 第二个参数“callback”传入回调即启用异步模式
        SpeechSynthesizer synthesizer = new SpeechSynthesizer(param, callback);
        // 非阻塞调用，立即返回null（实际结果通过回调接口异步传递），在回调接口的onEvent方法中实时获取二进制音频
        try {
            synthesizer.call(msg);
            // 等待合成完成
            latch.await();
            // 等待播放线程全部播放完
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 任务结束后关闭websocket连接
            synthesizer.getDuplexApi().close(1000, "bye");
        }
        // 首次发送文本时需建立 WebSocket 连接，因此首包延迟会包含连接建立的耗时
        System.out.println(
                "[Metric] requestId为："
                        + synthesizer.getLastRequestId()
                        + "，首包延迟（毫秒）为："
                        + synthesizer.getFirstPackageDelay());


        return filePath;
    }





}
