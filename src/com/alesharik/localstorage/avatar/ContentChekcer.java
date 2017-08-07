package com.alesharik.localstorage.avatar;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.SafeSearchAnnotation;
import com.google.protobuf.ByteString;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class ContentChekcer {
    @SneakyThrows
    public static String checkImage(BufferedImage image) {
        ByteString bytes = toJpeg(image);
        Image img = Image.newBuilder().setContent(bytes).build();
        Feature feature = Feature.newBuilder().setType(Feature.Type.SAFE_SEARCH_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(img).build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(Collections.singletonList(request));
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.err.printf("Error: %s\n", res.getError().getMessage());
                    return "";
                }

                SafeSearchAnnotation annotation = res.getSafeSearchAnnotation();
//                out.printf(
//                        "adult: %s\nmedical: %s\nspoofed: %s\nviolence: %s\n",
//                        annotation.getAdult(),
//                        annotation.getMedical(),
//                        annotation.getSpoof(),
//                        annotation.getViolence());
                //TODO
            }
        }
        return null;
    }

    @SneakyThrows
    private static ByteString toJpeg(BufferedImage bufferedImage) {
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        ImageOutputStream outputStream = ImageIO.createImageOutputStream(compressed);

        ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();

        ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
        jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpgWriteParam.setCompressionQuality(0.7f);

        jpgWriter.setOutput(outputStream);

        jpgWriter.write(null, new IIOImage(bufferedImage, null, null), jpgWriteParam);

        jpgWriter.dispose();

        byte[] jpegData = compressed.toByteArray();
        return ByteString.copyFrom(jpegData);
    }
}
