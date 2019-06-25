package ir.woope.woopeapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

/**
 * BroadcastReceiver to wait for SMS messages. This can be registered either
 * in the AndroidManifest or at runtime.  Should filter Intents on
 * SmsRetriever.SMS_RETRIEVED_ACTION.
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    /*@Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // get sms objects
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus.length == 0) {
                    return;
                }
                // large message might be broken into many
                SmsMessage[] messages = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }
                String sender = messages[0].getOriginatingAddress();
                String message = sb.toString();
                Log.d("smsReceiver",message);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                Intent i = new Intent("SMSBroadcastReceiver");
                // Data you need to pass to activity
                i.putExtra("message", message);

                context.sendBroadcast(i);
                // prevent any other broadcast receivers from receiving broadcast
                // abortBroadcast();
            }
        }
    }*/

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);

                    Log.d("smsReceiver",message);
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server.
                    Bundle extra = intent.getExtras();
                    Intent i = new Intent("SMSBroadcastReceiver");
                    // Data you need to pass to activity
                    i.putExtra("message", message);

                    context.sendBroadcast(i);
                    break;
                case CommonStatusCodes.TIMEOUT:
                    Log.d("smsReceiver","TIMEOUT");
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    break;
            }
        }
    }
}