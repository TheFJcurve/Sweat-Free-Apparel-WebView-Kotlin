package com.example.sfawebview

import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.messaging.RemoteMessage
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
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
        mockNotificationManager = mock()

        `when`(service.applicationContext).thenReturn(context)
        `when`(service.getSystemService(NOTIFICATION_SERVICE)).thenReturn(mockNotificationManager)
    }

    @Test
    fun `onNewToken should log token`() {
        val token = "dummy_token"
        service.onNewToken(token)
    }

    @Test
    fun `onMessageReceived should call showNotification`() {
        val remoteMessage = Mockito.mock(RemoteMessage::class.java)
        val notification = Mockito.mock(RemoteMessage.Notification::class.java)

        `when`(remoteMessage.notification).thenReturn(notification)
        `when`(notification.title).thenReturn("Test Title")
        `when`(notification.body).thenReturn("Test Body")

        service.onMessageReceived(remoteMessage)

        verify(service).showNotification("Test Title", "Test Body")
    }

    @Test
    fun `showNotification should create a notification without crashing`() {
        service.showNotification("Sample Title", "Sample Message")

        verify(mockNotificationManager).notify(Mockito.anyInt(), Mockito.any())
    }
}