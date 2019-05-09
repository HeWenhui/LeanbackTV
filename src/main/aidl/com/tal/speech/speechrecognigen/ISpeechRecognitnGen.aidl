// ISpeechRecogInterface.aidl
package com.tal.speech.speechrecognigen;

import com.tal.speech.speechrecognigen.ISpeechRecognitnCall;

// Declare any non-default types here with import statements

interface ISpeechRecognitnGen {

      void startSpeech(ISpeechRecognitnCall call);

      void check(ISpeechRecognitnCall call);

      void stopSpeech();

      void release();
}
