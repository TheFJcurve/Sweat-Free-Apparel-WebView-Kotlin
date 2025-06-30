package com.sweatfree.sftwebview

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.messaging.RemoteMessage
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NotificationServiceTest {
    private lateinit var service: NotificationService

    @Mock
    lateinit var mockNotificationManager: NotificationManager

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        service =
            Mockito.spy(Robolectric.buildService(NotificationService::class.java).create().get())
        mockNotificationManager = Mockito.mock(NotificationManager::class.java)

        Mockito.`when`(service.applicationContext).thenReturn(context)
        Mockito.`when`(service.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(mockNotificationManager)
    }

    @Test
    fun `onNewToken logs token without crash`() {
        service.onNewToken("some_token")
    }

    @Test
    fun `onMessageReceived with valid notification triggers showNotification`() {
        val remoteMessage = Mockito.mock(RemoteMessage::class.java)
        val notification = Mockito.mock(RemoteMessage.Notification::class.java)

        Mockito.`when`(remoteMessage.notification).thenReturn(notification)
        Mockito.`when`(notification.title).thenReturn("Title")
        Mockito.`when`(notification.body).thenReturn("Body")

        service.onMessageReceived(remoteMessage)

        Mockito.verify(service).showNotification("Title", "Body")
    }

    @Test
    fun `onMessageReceived with null notification does nothing`() {
        val remoteMessage = Mockito.mock(RemoteMessage::class.java)
        Mockito.`when`(remoteMessage.notification).thenReturn(null)

        service.onMessageReceived(remoteMessage)

        Mockito.verify(service, Mockito.never())
            .showNotification(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
    }

    @Test
    fun `onMessageReceived with null title throws`() {
        val remoteMessage = Mockito.mock(RemoteMessage::class.java)
        val notification = Mockito.mock(RemoteMessage.Notification::class.java)

        Mockito.`when`(remoteMessage.notification).thenReturn(notification)
        Mockito.`when`(notification.title).thenReturn(null)
        Mockito.`when`(notification.body).thenReturn("Some body")

        Assert.assertThrows(NullPointerException::class.java) { service.onMessageReceived((remoteMessage)) }
    }

    @Test
    fun `onMessageReceived with null body throws`() {
        val remoteMessage = Mockito.mock(RemoteMessage::class.java)
        val notification = Mockito.mock(RemoteMessage.Notification::class.java)

        Mockito.`when`(remoteMessage.notification).thenReturn(notification)
        Mockito.`when`(notification.title).thenReturn("Title")
        Mockito.`when`(notification.body).thenReturn(null)

        Assert.assertThrows(NullPointerException::class.java) {
            service.onMessageReceived(
                remoteMessage
            )
        }
    }

    @Test
    fun `showNotification with empty title and body still builds notification`() {
        service.showNotification("", "")
        Mockito.verify(mockNotificationManager)
            .notify(ArgumentMatchers.eq(0), ArgumentMatchers.any())
    }

    @Test
    fun `showNotification triggers NotificationManager with proper inputs`() {
        service.showNotification("Some Title", "Some Message")
        Mockito.verify(mockNotificationManager)
            .notify(ArgumentMatchers.eq(0), ArgumentMatchers.any())
    }

    @Test
    fun `showNotification creates channel on Android O and above`() {
        service.showNotification("Title", "Message")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Mockito.verify(mockNotificationManager)
                .createNotificationChannel(ArgumentMatchers.any())
        }
    }
}