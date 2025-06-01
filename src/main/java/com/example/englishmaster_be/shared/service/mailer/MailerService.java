package com.example.englishmaster_be.shared.service.mailer;

import com.example.englishmaster_be.model.mock_test.MockTestEntity;
import com.example.englishmaster_be.model.mock_test_result.MockTestResultEntity;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.value.LinkValue;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class MailerService {


    JavaMailSender mailSender;

    ResourceLoader resourceLoader;

    SpringTemplateEngine templateEngine;

    LinkValue linkValue;



    public void sendMail(String recipientEmail) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(recipientEmail);
        helper.setSubject("There is a long time you missing sign in ?");
        helper.setSubject("Take your sign in and looking new features from MEU-English, let's go!");

        mailSender.send(message);
    }

    public void sendMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendNotificationEmail(UserEntity user){
        String subject = "Đã lâu bạn không truy cập rồi!";
        String body = String.format(
                "Hello %s, We from MEU-English." +
                        "\n\nFor a long time you missing for a sign in." +
                        "\n\nTake your come bank to looking for a new feature from us." +
                        "\n\nBest regards," +
                        "\nOur team.",
                user.getName()
        );
        sendMail(user.getEmail(),subject,body);
    }

    public void sendOtpToEmail(String email, String otp)
            throws MessagingException, IOException
    {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String titleText = "Mã xác thực OTP";
        String otpMessage = "Đây là mã xác thực OTP được cấp để bạn đổi mật khẩu mới, hiệu lực trong vòng 1 phút.";

        // Nếu bạn vẫn muốn sử dụng template, thay thế nội dung theo cách này:
        String templateContent = readTemplateContent("sendOtpEmail.html");
        templateContent = templateContent.replace("{{titleText}}", titleText)
                                        .replace("{{otpMessage}}", otpMessage)
                                        .replace("{{otpCode}}", otp);

        helper.setTo(email);
        helper.setSubject("Quên mật khẩu");
        helper.setText(templateContent, true);
        mailSender.send(message);
    }


    public void sendConfirmRegister(String email, String confirmationToken) throws IOException, MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String confirmationLink = linkValue.getLinkFE() + "register/confirm?token=" + confirmationToken;

        String templateContent = readTemplateContent("email_templates.html")
                .replace("*|MC_PREVIEW_TEXT|*", "MeU English")
                .replace("{{linkConfirm}}", confirmationLink)
                .replace("{{btnConfirm}}", "Xác nhận")
                .replace("{{nameLink}}", "Vui lòng chọn xác nhận để tiến hành đăng ký tài khoản.")
                .replace("*|current_year|*", String.valueOf(LocalDateTime.now().getYear()));

        helper.setTo(email);
        helper.setSubject("Xác nhận tài khoản");
        helper.setText(templateContent, true);
        mailSender.send(message);
    }


    public void sendForgetPassEmail(String email, String confirmationToken) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String confirmationLink = linkValue.getLinkFE() + "/forgetPass/confirm?token=" + confirmationToken;

        String templateContent = readTemplateContent("email_templates.html");
        templateContent = templateContent.replace("{{linkConfirm}}", confirmationLink);
        templateContent = templateContent.replace("{{btnConfirm}}", "Reset Password");
        templateContent = templateContent.replace("{{nameLink}}", "Reset Password");

        helper.setTo(email);
        helper.setSubject("Quên tài khoản");
        helper.setText(templateContent, true);
        mailSender.send(message);
    }

    public String readTemplateContent(String templateFileName) throws IOException {

        Resource templateResource = resourceLoader.getResource("classpath:templates/" + templateFileName);
        byte[] templateBytes = FileCopyUtils.copyToByteArray(templateResource.getInputStream());

        return new String(templateBytes, StandardCharsets.UTF_8);
    }

    public void sendResultEmail(String email, MockTestEntity mockTest) throws IOException, MessagingException {
        // Tạo đối tượng MimeMessage
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Đọc nội dung mẫu email từ file (ví dụ: test_email.html)
        String templateContent = readTemplateContent("test_email.html");

        // Thay thế các placeholder trong template
        templateContent = templateContent.replace("{{nameToeic}}", mockTest.getTopic().getTopicName());
        templateContent = templateContent.replace("{{userName}}", mockTest.getUser().getName());
        templateContent = templateContent.replace("{{correctAnswer}}", String.valueOf(mockTest.getTotalAnswersCorrect()));
        templateContent = templateContent.replace("{{score}}", mockTest.getTotalScore() + "");
        templateContent = templateContent.replace("{{timeAnswer}}", mockTest.getFinishTime()  + "");
        // Nếu cần, bạn có thể thay thế thêm các placeholder khác như tổng điểm, thời gian trả lời,...

        Set<MockTestResultEntity> mockTestResults = mockTest.getMockTestResults()
                .stream().sorted(Comparator.comparing(mockTestResult -> mockTestResult.getPart().getPartName()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Xây dựng nội dung hiển thị kết quả của từng phần dựa trên Map
        StringBuilder partsHtml = new StringBuilder();
        // Nếu hệ thống có nhiều Part, bạn có thể duyệt qua các entry của Map
        for (MockTestResultEntity mockTestResult : mockTestResults) {
            PartEntity part = mockTestResult.getPart();
            int correctCount = mockTestResult.getTotalCorrect();
            int score = mockTestResult.getTotalScoreResult();

            // Ở đây hiển thị theo mẫu: "Part {ID} - Số câu đúng: {correctCount} - Số điểm: {score}"
            // Nếu có tên Part hoặc thứ tự Part, bạn có thể thay thế phần hiển thị này.
            String partHtml = "<p>Part " + part.getPartName() + ": Số câu đúng: " + correctCount
                    + " - Số điểm: " + score + "</p>";
            partsHtml.append(partHtml);
        }
        templateContent = templateContent.replace("{{parts}}", partsHtml.toString());

        // Cấu hình thông tin email
        helper.setTo(email);
        helper.setSubject("Thông tin bài thi");
        helper.setText(templateContent, true);

        // Gửi email
        mailSender.send(message);
    }
}
