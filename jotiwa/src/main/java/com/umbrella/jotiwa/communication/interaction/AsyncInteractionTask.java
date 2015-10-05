package com.umbrella.jotiwa.communication.interaction;

import android.os.AsyncTask;
import android.os.Message;

import com.umbrella.jotiwa.map.area348.MapManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.1
 * @since 22-9-2015
 * Class that servers as a async interaction task.
 * @see android.os.AsyncTask
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

                /**
                 * Checks if there should be data send.
                 * */
                if(params[i].getData() != null)
                {
                    connection.setRequestMethod("POST");
                    OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
                    streamWriter.write(params[i].getData());
                    streamWriter.flush();
                    streamWriter.close();
                }

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
    protected void onPostExecute(InteractionResult[] results) {
        ArrayList<InteractionResult> handle = new ArrayList<>();
        for(int i = 0; i < results.length-1; i++)
        {
            if(results[i].getRequest().needsHandling())
            {
                handle.add(results[i]);
            }
        }
        Message message = new Message();
        message.obj = handle;
        MapManager.getDataUpdater().sendMessage(message);
    }
}
