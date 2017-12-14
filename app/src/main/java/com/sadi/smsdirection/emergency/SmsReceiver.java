package com.sadi.smsdirection.emergency;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.sadi.smsdirection.Operations;


public class SmsReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {


        String pn12 = Operations.getStringFromSharedPreference(context, "ePhoneNumbers");
        String emergencyRandom = Operations.getStringFromSharedPreference(context, "emergencyRandom");

        Bundle extras = intent.getExtras();
        if (extras == null)
            return;

        Object[] pdus = (Object[]) extras.get("pdus");
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage SMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = SMessage.getOriginatingAddress();
            String body = SMessage.getMessageBody().toString();

            // A custom Intent that will used as another Broadcast
            String destination = PhoneNumberUtils.compare(sender, pn12) ? pn12 : "";

            /*emergencycall*/
            if (destination.length() > 0 && emergencyRandom.length() > 0 && String.valueOf(emergencyRandom).equals(body)) {

                Operations.SaveToSharedPreference(context, "ePhoneNumber", pn12);

                Intent intentone = new Intent(context.getApplicationContext(), com.sadi.smsdirection.EmergencyCallActivity.class);
                intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentone);
                Toast.makeText(context, "Emergency number is verified", Toast.LENGTH_SHORT).show();
            }

            Intent in = new Intent("SmsMessage.intent.MAIN").putExtra("get_msg", sender + ":" + body);
            context.sendBroadcast(in);

        }


    }
}
