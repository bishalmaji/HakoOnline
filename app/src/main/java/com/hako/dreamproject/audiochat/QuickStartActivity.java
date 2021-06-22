//
//  QuickStartActivity.java
//  ZegoExpressExampleAudio
//  im.zego.express_example_audio.quick_start
//
//  Created by Patrick Fu on 2020/06/01.
//  Copyright © 2020 Zego. All rights reserved.
//

package com.hako.dreamproject.audiochat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.hako.dreamproject.R;

import org.json.JSONObject;

import java.util.Date;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.callback.IZegoDestroyCompletionCallback;
import im.zego.zegoexpress.callback.IZegoEventHandler;
import im.zego.zegoexpress.constants.ZegoPlayerState;
import im.zego.zegoexpress.constants.ZegoPublisherState;
import im.zego.zegoexpress.constants.ZegoRoomState;
import im.zego.zegoexpress.constants.ZegoScenario;
import im.zego.zegoexpress.entity.ZegoUser;

public class QuickStartActivity {

    boolean isTestEnv = true;


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Generate random user ID to avoid user ID conflict and mutual influence when different mobile phones are used
//        String randomSuffix = String.valueOf(new Date().getTime()%(new Date().getTime()/1000));
//        userID = "user" + randomSuffix;
//
//    }

    String roomID = "QuickStartRoom-1";

    public void onDestroy() {
        // Release SDK resources
        ZegoExpressEngine.destroyEngine(null);
    }

    public void createEngineButtonClick(Activity activity, String userID, String playStreamID, String publishStreamID) {
        Log.i("[ZEGO]", "🚀 Create ZegoExpressEngine");

        // Create ZegoExpressEngine and set a eventHandler
        ZegoExpressEngine.createEngine(GetAppIDConfig.appID, GetAppIDConfig.appSign, isTestEnv,
                ZegoScenario.GENERAL, activity.getApplication(), eventHandler);

        ZegoUser user = new ZegoUser(userID);

        Log.i("[ZEGO]", "🚪 Start login room");

        // Login room
        ZegoExpressEngine.getEngine().loginRoom(roomID, user);

//
//        Log.i("[ZEGO]", "📤 Start publishing stream");
//
//        ZegoExpressEngine.getEngine().startPublishingStream(publishStreamID);

        Log.i("[ZEGO]", "📥 Start playing stream");

        ZegoExpressEngine.getEngine().startPlayingStream(playStreamID);
    }

    public void startPublishingButtonClick(String publishStreamID) {

        Log.i("[ZEGO]", "📤 Start publishing stream");

        ZegoExpressEngine.getEngine().startPublishingStream(publishStreamID);
    }

    IZegoEventHandler eventHandler = new IZegoEventHandler() {

        @Override
        public void onRoomStateUpdate(String roomID, ZegoRoomState state, int errorCode, JSONObject extendedData) {
            if (state == ZegoRoomState.CONNECTED && errorCode == 0) {
                Log.i("[ZEGO]", "🚩 🚪 Login room success");
            }

            if (errorCode != 0) {
                Log.i("[ZEGO]", "🚩 ❌ 🚪 Login room fail, errorCode: " + errorCode);
            }
        }

        @Override
        public void onPublisherStateUpdate(String streamID, ZegoPublisherState state, int errorCode, JSONObject extendedData) {
            if (state == ZegoPublisherState.PUBLISHING && errorCode == 0) {
                Log.i("[ZEGO]", "🚩 📤 Publishing stream success");
            }

            if (errorCode != 0) {
                Log.i("[ZEGO]", "🚩 ❌ 📤 Publishing stream fail, errorCode: " + errorCode);
            }
        }

        @Override
        public void onPlayerStateUpdate(String streamID, ZegoPlayerState state, int errorCode, JSONObject extendedData) {
            if (state == ZegoPlayerState.PLAYING && errorCode == 0) {
                Log.i("[ZEGO]", "🚩 📥 Playing stream success");
            }

            if (errorCode != 0) {
                Log.i("[ZEGO]", "🚩 ❌ 📥 Playing stream fail, errorCode: " + errorCode);
            }
        }
    };
}
