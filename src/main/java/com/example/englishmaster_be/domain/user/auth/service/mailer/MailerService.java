package com.example.englishmaster_be.domain.user.auth.service.mailer;

import com.example.englishmaster_be.common.constant.TopicType;
import com.example.englishmaster_be.domain.mock_test.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.mock_test.mock_test.repository.MockTestRepository;
import com.example.englishmaster_be.domain.mock_test.mock_test.service.IMockTestService;
import com.example.englishmaster_be.domain.exam.topic.type.model.TopicTypeEntity;
import com.example.englishmaster_be.domain.exam.topic.type.service.ITopicTypeService;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.user.repository.UserRepository;
import com.example.englishmaster_be.value.LinkValue;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j(topic = "MAILER-SERVICE")
@Service
public class MailerService {

    private final JavaMailSender mailSender;
    private final ResourceLoader resourceLoader;
    private final LinkValue linkValue;
    private final UserRepository userRepository;
    private final MockTestRepository mockTestRepository;
    private final IMockTestService mockTestService;
    private final ITopicTypeService topicTypeService;

    @Lazy
    public MailerService(JavaMailSender mailSender, ResourceLoader resourceLoader, LinkValue linkValue, UserRepository userRepository, MockTestRepository mockTestRepository, IMockTestService mockTestService, ITopicTypeService topicTypeService) {
        this.mailSender = mailSender;
        this.resourceLoader = resourceLoader;
        this.linkValue = linkValue;
        this.userRepository = userRepository;
        this.mockTestRepository = mockTestRepository;
        this.mockTestService = mockTestService;
        this.topicTypeService = topicTypeService;
    }

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


    public void sendNotificationEmail(UUID userId){

        UserEntity user = userRepository.findById(userId).orElse(null);

        if(user == null) return;

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

    public void sendResultMockTestEmail(UUID mockTestId) throws MessagingException, IOException {
        Assert.notNull(mockTestId, "mockTestId must not be null");
        MockTestEntity mockTest = mockTestService.getMockTestById(mockTestId);
        TopicTypeEntity topicType = topicTypeService.getTopicTypeToId(mockTest.getTopic().getTopicTypeId());
        String feMockTestResultUrl;

        if(topicType.getTopicTypeName().equalsIgnoreCase(TopicType.SPEAKING.getType()))
            feMockTestResultUrl = linkValue.getLinkFeMockTestResultSpeaking();
        else if(topicType.getTopicTypeName().equalsIgnoreCase(TopicType.WRITING.getType()))
            feMockTestResultUrl = linkValue.getLinkFeMockTestResultWriting();
        else feMockTestResultUrl = linkValue.getLinkFeMockTestResultReadingListening();
        feMockTestResultUrl = feMockTestResultUrl.replace(":mockTestId", mockTestId.toString());
        String feShowMoreTopic = linkValue.getLinkFeShowMoreTopic();
        String htmlContent = readTemplateContent("speaking-result-email.html")
                .replace("${topicName}", mockTest.getTopic().getTopicName())
                .replace("${linkFeMockTestResult}", feMockTestResultUrl)
                .replace("${linkFeShowMoreTopic}", feShowMoreTopic)
                .replace("${currentYear}", String.valueOf(LocalDateTime.now().getYear()))
                .replace("${currentDate}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        String sendTo = mockTest.getUser().getEmail();
        sendToEmail("testmeusolution@gmail.com", sendTo, "Kết quả bài thi", htmlContent);
    }

    public void sendToEmail(String fromEmail, String toEmail, String subjectInEmail, String contentEmail) throws MessagingException {

        log.info("Send to email {}", toEmail);

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, Boolean.TRUE);

        mimeMessageHelper.setFrom(fromEmail);
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject(subjectInEmail);
        mimeMessageHelper.setText(contentEmail, Boolean.TRUE);

        mailSender.send(mimeMessage);
    }
}
