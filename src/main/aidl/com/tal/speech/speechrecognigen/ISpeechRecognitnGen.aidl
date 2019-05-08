// ISpeechRecogInterface.aidl
package com.tal.speech.speechrecognigen;

import com.tal.speech.speechrecognigen.ISpeechRecognitnCall;

// Declare any non-default types here with import statements

interface ISpeechRecognitnGen {

      void start(ISpeechRecognitnCall call);

      void stop();

      void release();
}
