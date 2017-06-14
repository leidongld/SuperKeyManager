package com.example.leidong.superkeymanager.fingerPrint;

import android.os.Handler;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import com.example.leidong.superkeymanager.activity.FingerPrintActivity;

/**
 * Created by leidong on 2016/11/5
 */

public class MyAuthCallback extends FingerprintManagerCompat.AuthenticationCallback {

    private Handler handler = null;

    /**
     * 构造器
     * @param handler
     */
    public MyAuthCallback(Handler handler) {
        super();
        this.handler = handler;
    }

    /**
     * 认证错误
     * @param errMsgId
     * @param errString
     */
    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        super.onAuthenticationError(errMsgId, errString);

        if (handler != null) {
            handler.obtainMessage(FingerPrintActivity.MSG_AUTH_ERROR, errMsgId, 0).sendToTarget();
        }
    }

    /**
     * 认证帮助信息
     * @param helpMsgId
     * @param helpString
     */
    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        super.onAuthenticationHelp(helpMsgId, helpString);

        if (handler != null) {
            handler.obtainMessage(FingerPrintActivity.MSG_AUTH_HELP, helpMsgId, 0).sendToTarget();
        }
    }

    /**
     * 认证成功
     * @param result
     */
    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);

        if (handler != null) {
            handler.obtainMessage(FingerPrintActivity.MSG_AUTH_SUCCESS).sendToTarget();
        }
    }

    /**
     * 认证失败
     */
    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();

        if (handler != null) {
            handler.obtainMessage(FingerPrintActivity.MSG_AUTH_FAILED).sendToTarget();
        }
    }
}
