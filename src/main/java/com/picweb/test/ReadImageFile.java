package com.picweb.test;
import com.picweb.dao.ImageHashDao;
import com.picweb.dao.entity.ImageHashEntity;
import com.picweb.service.ImagepHashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
@Controller
public class ReadImageFile {
    @Autowired
    private ImagepHashService imagepHashService;

    public static void main(String[] args) throws Exception {
        new ReadImageFile().getImagepHash();
    }
    @Autowired
    private ImageHashDao imageHashDao;
    public void getImagepHash() throws Exception {
        File file = new File("C:\\Users\\lenovo\\Desktop\\图包\\F2_nWTrasAEYqyU.jpg");

        try {
            BufferedImage image = ImageIO.read(file);
            System.out.println("图片宽度: " + image.getWidth());
            System.out.println("图片高度: " + image.getHeight());
            System.out.println("图片感知哈希值: " + imagepHashService.perceptualHash(image));
            ImageHashEntity imageHashEntity = new ImageHashEntity();
            imageHashEntity.getAHash();


        } catch (IOException e) {
            System.out.println("读取图片失败");
            e.printStackTrace();
        }
    }

}