package com.knox.notification.handler;

import com.knox.notification.constant.ChannelType;
import com.knox.notification.service.NotificationSender;
import com.knox.notification.service.impl.MailSenderService;
import com.knox.notification.service.impl.WhatsAppSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class NotificationFactoryTest {

    @Mock
    private MailSenderService mailSenderService;

    @Mock
    private WhatsAppSender whatsAppSender;

    private NotificationFactory notificationFactory;

    @BeforeEach
    void setUp() {
        List<NotificationSender> senders = Arrays.asList(mailSenderService, whatsAppSender);
        notificationFactory = new NotificationFactory(senders);
    }

    @Test
    void get_ShouldReturnMailSenderService_WhenChannelIsEmail() {
        NotificationSender sender = notificationFactory.get(ChannelType.EMAIL);
        assertTrue(sender instanceof MailSenderService);
    }

    @Test
    void get_ShouldReturnWhatsAppSender_WhenChannelIsWhatsApp() {
        NotificationSender sender = notificationFactory.get(ChannelType.WHATSAPP);
        assertTrue(sender instanceof WhatsAppSender);
    }

    @Test
    void get_ShouldThrowException_WhenChannelIsUnsupported() {
        assertThrows(IllegalArgumentException.class, () -> notificationFactory.get(ChannelType.SMS));
    }
}
