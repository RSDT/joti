package com.umbrella.jotiwa.communication.interaction;

import android.os.AsyncTask;
import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by stesi on 22-9-2015.
 */
public class AsyncInteractionTask extends AsyncTask<InteractionRequest, Integer, InteractionResult[]> {

    protected InteractionResult[] doInBackground(InteractionRequest... params) {
        InteractionResult[] results = new InteractionResult[params.length];
        for(int i = 0; i < params.length; i++)
        {
            InteractionResult result = new InteractionResult();
            try
            {
                HttpURLConnection connection = (HttpURLConnection)params[i].getUrl().openConnection();
                InputStream response = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response));
                StringBuilder builder = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null)
                {
                    builder.append(line);
                    builder.append("/r");
                }
                bufferedReader.close();
                result.setRequest(params[i]);
                result.setResultState(InteractionResultState.INTERACTION_RESULT_STATE_SUCCESS);
                result.setReceivedData(builder.toString());
                results[i] = result;
            } catch(Exception e) {
                result.setRequest(params[i]);
                result.setResultState(InteractionResultState.INTERACTION_RESULT_STATE_FAIL);
                results[i] = result;  }
        }
        return results;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onProgressUpdate(Integer... values) {

    }

    @Override
    protected void onPostExecute(InteractionResult[] result) {
        Message message = new Message();
        message.obj = result;
        result[0].getRequest().getHandler().sendMessage(message);
    }
}
