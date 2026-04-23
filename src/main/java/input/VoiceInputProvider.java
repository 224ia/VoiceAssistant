package input;

import util.Logger;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class VoiceInputProvider implements InputProvider {
    private final TargetDataLine microphone;
    private final AudioFormat format;

    private static final Path voiceFilePath = Path.of("micro.wav");

    public VoiceInputProvider() {
        try {
            format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();
            Logger.info("Microphone is enabled");

            // Очищаємо старий файл при запуску
            Files.deleteIfExists(voiceFilePath);

        } catch (LineUnavailableException | IOException e) {
            Logger.error("Failed to enable microphone", e);
            throw new RuntimeException(e);
        }
    }

    public String input() {
        return input(5000); // 5 секунд за замовчуванням
//        try {
//            testMicrophone();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        return "";
    }

    public String input(int timeoutMillis) {
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream audioBuffer = new ByteArrayOutputStream();

        long startTime = System.currentTimeMillis();

        System.out.println("🎤 Слухаю " + (timeoutMillis / 1000) + " секунд...");

        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            int bytesRead = microphone.read(buffer, 0, buffer.length);
            if (bytesRead > 0) {
                audioBuffer.write(buffer, 0, bytesRead);
            }
        }

        System.out.println("⏹️ Зупинка запису");

        // Зберігаємо як правильний WAV файл
        try {
            saveAsWav(audioBuffer.toByteArray());
            Logger.info("Saved audio to " + voiceFilePath);
        } catch (IOException e) {
            Logger.error("Failed to save audio", e);
        }

        // TODO: розпізнавання через Vosk або іншу бібліотеку
        return processWithVosk();
    }

    public void testMicrophone() throws InterruptedException {
        byte[] buffer = new byte[4096];

        System.out.println("🎤 Говоріть щось...");

        for (int i = 0; i < 50; i++) {  // ~5 секунд
            int bytesRead = microphone.read(buffer, 0, buffer.length);

            double maxAmplitude = 0;
            for (int j = 0; j < bytesRead; j += 2) {
                short sample = (short) ((buffer[j+1] << 8) | (buffer[j] & 0xFF));
                maxAmplitude = Math.max(maxAmplitude, Math.abs(sample));
            }

            if (maxAmplitude > 1000) {
                System.out.println("🔊 Чую звук! Амплітуда: " + maxAmplitude);
            }

            Thread.sleep(100);
        }
    }

    private void saveAsWav(byte[] audioData) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
        AudioInputStream ais = new AudioInputStream(bais, format, audioData.length / format.getFrameSize());

        File wavFile = voiceFilePath.toFile();
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);

        ais.close();
    }

    private String processWithVosk() {
        // TODO: Інтеграція Vosk
        System.out.println("📝 Розпізнавання... (поки що заглушка)");
        return "привіт"; // Тимчасово
    }

    public void close() {
        microphone.close();
    }

    public String recognize() {
        // Записуємо 3 секунди
//        recordSeconds(3);

        // Розпізнаємо з файлу
//        return recognizeWithVosk(Path.of("record.wav"));
        return "";
    }

//    private String recognizeWithVosk(Path wavFile) {
//        try {
//            // Vosk приймає файл або потік байтів
//            InputStream ais = AudioSystem.getAudioInputStream(wavFile.toFile());
//
//            Recognizer recognizer = new Recognizer(model, 16000.0f);
//
//            byte[] buffer = new byte[4096];
//            int bytesRead;
//
//            while ((bytesRead = ais.read(buffer)) >= 0) {
//                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
//                    String result = recognizer.getResult();
//                    recognizer.close();
//                    return extractText(result);
//                }
//            }
//
//            recognizer.close();
//            return "";
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
//    }
}