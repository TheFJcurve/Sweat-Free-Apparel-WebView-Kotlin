package com.example.sfawebview

import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.messaging.RemoteMessage
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
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
        service = spy(Robolectric.buildService(NotificationService::class.java).create().get())
        mockNotificationManager = mock(NotificationManager::class.java)

        `when`(service.applicationContext).thenReturn(context)
        `when`(service.getSystemService(NOTIFICATION_SERVICE)).thenReturn(mockNotificationManager)
    }

    @Test
    fun `onNewToken logs token without crash`() {
        service.onNewToken("some_token")
    }

    @Test
    fun `onMessageReceived with valid notification triggers showNotification`() {
        val remoteMessage = mock(RemoteMessage::class.java)
        val notification = mock(RemoteMessage.Notification::class.java)

        `when`(remoteMessage.notification).thenReturn(notification)
        `when`(notification.title).thenReturn("Title")
        `when`(notification.body).thenReturn("Body")

        service.onMessageReceived(remoteMessage)

        verify(service).showNotification("Title", "Body")
    }

    @Test
    fun `onMessageReceived with null notification does nothing`() {
        val remoteMessage = mock(RemoteMessage::class.java)
        `when`(remoteMessage.notification).thenReturn(null)

        service.onMessageReceived(remoteMessage)

        verify(service, never()).showNotification(anyString(), anyString())
    }

    @Test
    fun `onMessageReceived with null title throws`() {
        val remoteMessage = mock(RemoteMessage::class.java)
        val notification = mock(RemoteMessage.Notification::class.java)

        `when`(remoteMessage.notification).thenReturn(notification)
        `when`(notification.title).thenReturn(null)
        `when`(notification.body).thenReturn("Some body")

        assertThrows(NullPointerException::class.java) { service.onMessageReceived((remoteMessage)) }
    }

    @Test
    fun `onMessageReceived with null body throws`() {
        val remoteMessage = mock(RemoteMessage::class.java)
        val notification = mock(RemoteMessage.Notification::class.java)

        `when`(remoteMessage.notification).thenReturn(notification)
        `when`(notification.title).thenReturn("Title")
        `when`(notification.body).thenReturn(null)

        assertThrows(NullPointerException::class.java) { service.onMessageReceived(remoteMessage) }
    }

    @Test
    fun `showNotification with empty title and body still builds notification`() {
        service.showNotification("", "")
        verify(mockNotificationManager).notify(eq(0), any())
    }

    @Test
    fun `showNotification triggers NotificationManager with proper inputs`() {
        service.showNotification("Some Title", "Some Message")
        verify(mockNotificationManager).notify(eq(0), any())
    }

    @Test
    fun `showNotification creates channel on Android O and above`() {
        service.showNotification("Title", "Message")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            verify(mockNotificationManager).createNotificationChannel(any())
        }
    }
}
