package uts.sep.tcba.sepprototype.Unit_Tests;

import android.content.Context;

import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;



import java.security.KeyPairGenerator;
import java.util.LinkedList;

import uts.sep.tcba.sepprototype.User;
import com.google.firebase.database.*;
import java.io.Serializable;
import android.util.Log;


@RunWith(PowerMockRunner.class)
@PrepareForTest(User.class)

/**
 * Created by Nick Earley on 26/09/2017.
 */
public class UserTest {

    private DatabaseReference mockedDatabaseReference;

    @Before
    public void before() {
        mockedDatabaseReference = Mockito.mock(DatabaseReference.class);

        FirebaseDatabase mockedFirebaseDatabase = Mockito.mock(FirebaseDatabase.class);
        when(mockedFirebaseDatabase.getReference()).thenReturn(mockedDatabaseReference);

        PowerMockito.mockStatic(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance()).thenReturn(mockedFirebaseDatabase);
    }


    @Test
    public void getID() throws Exception {

        int ID = 123;
        User testUser = new User(ID);
       // assertEquals(testUser.isID(ID), true);

    }


}

