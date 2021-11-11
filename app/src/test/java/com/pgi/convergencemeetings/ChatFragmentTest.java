//package com.pgi.convergencemeetings;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.widget.EditText;
//
//import com.pgi.convergence.persistance.SharedPreferencesManager;
//import com.pgi.convergencemeetings.meetings.chat.ui.ChatsAdapter;
//import com.pgi.convergencemeetings.meetings.chat.ui.ChatFragment;
//import com.pgi.convergencemeetings.models.Chat;
//import com.pgi.convergencemeetings.models.uapi.Participant;
//import com.pgi.convergencemeetings.utils.AppAuthUtils;
//import com.pgi.convergencemeetings.utils.WebMeetingEventsCache;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.mockito.Spy;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.rule.PowerMockRule;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.Mockito.atLeast;
//import static org.mockito.Mockito.verify;
//
///**
// * Created by nnennaiheke on 6/27/18.
// */
//
//@RunWith(MockitoJUnitRunner.class)
//@PrepareForTest({SharedPreferencesManager.class, AppAuthUtils.class, ChatPresenter.class, ChatsAdapter.class, WebMeetingEventsCache.class})
//public class ChatFragmentTest extends RobolectricTest {
//    @Rule
//    public PowerMockRule rule = new PowerMockRule();
//    RecyclerView mChatRecyclerView;
//    @Spy
//    private List<Chat> chats = new ArrayList<>();
//    ChatFragment chatFragment;
//    EditText mChatEditText;
//
//    private ChatsAdapter mChatsAdapter;
//    @Spy
//    private List<Chat> mCoversationList = new ArrayList<>();
//    private Participant mSelfParticipant;
//    private String mSelfInitials;
//
//    private ChatContractor.Presenter mPresenter;
//    private ChatFragmentParentActivityInteractor mParentActivityInteractor;
//    private WebMeetingEventsCache webMeetingEventsCache;
//    private Context mContext;
//    private Chat chat;
//    private boolean isObjectAdded;
//
//    @Before
//    public void setUp() {
//        chatFragment = Mockito.mock(ChatFragment.class);
//        webMeetingEventsCache = Mockito.mock(WebMeetingEventsCache.class);
//        mParentActivityInteractor = Mockito.mock(ChatFragmentParentActivityInteractor.class);
//        PowerMockito.mockStatic(WebMeetingEventsCache.class);
//        Mockito.when(WebMeetingEventsCache.getInstance()).thenReturn(webMeetingEventsCache);
//        chat = Mockito.mock(Chat.class);
//
//    }
//
//
//    @Test
//    public void testOnChatMessageReceived() {
//        //Add chat object to conversation list and notify UI.
//        mCoversationList.add(chat);
//        chatFragment.scrollToLastPosition(true);
//        if (mParentActivityInteractor != null) {
//            mParentActivityInteractor.updateChatTabNotificationCount();
//            verify(mParentActivityInteractor, atLeast(1)).updateChatTabNotificationCount();
//        }
//        verify(mCoversationList).add(chat);
//        verify(chatFragment).scrollToLastPosition(true);
//    }
//
//    @Test
//    public void testOnChatMessagesReceived() {
//        if (chats != null) {
//            mCoversationList.clear();
//            mCoversationList.addAll(chats);
//            chatFragment.scrollToLastPosition(false);
//            verify(mCoversationList).clear();
//            verify(mCoversationList).addAll(chats);
//            verify(chatFragment).scrollToLastPosition(false);
//        }
//    }
//
//    @Test
//    public void testOnClearChat() throws Exception {
//        mCoversationList.clear();
//        mCoversationList.removeAll(chats);
//        chatFragment.scrollToLastPosition(false);
//        verify(mCoversationList).clear();
//        verify(mCoversationList).removeAll(chats);
//        verify(chatFragment).scrollToLastPosition(false);
//
//    }
//}