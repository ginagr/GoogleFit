package name.heqian.cs528.googlefit;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import static android.app.PendingIntent.getActivity;

public class ActivityRecognizedService extends IntentService {

    public static final String REQUEST_STRING = "myRequest";
    public static final String RESPONSE_STRING = "myResponse";
    public static final String RESPONSE_MESSAGE = "myResponseMessage";

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            String requestString = intent.getStringExtra(REQUEST_STRING);
            String responseString = requestString + "hello?";
            String responseMessage = "This is my response";

            Log.d(null, "- - - - - - - - - - - - - on handle event being called - - - - - - - - - - - - -");

            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            String currentActivity = handleDetectedActivities( result.getProbableActivities() );

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.ActivityReceiver.PROCESS_RESPONSE);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(RESPONSE_STRING, currentActivity);
            broadcastIntent.putExtra(RESPONSE_MESSAGE, responseMessage);
            sendBroadcast(broadcastIntent);
        }
    }

    private String handleDetectedActivities(List<DetectedActivity> probableActivities) {
        Log.d(null, "- - - - - - - - - - - - - handle detected activities being called - - - - - - - - - - - - -");
        String currentActivity = "";

        for( DetectedActivity activity : probableActivities ) {
            switch( activity.getType() ) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
                    currentActivity = ActivitiesEnum.IN_VEHICLE.toString();
                    break;

                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e( "ActivityRecogition", "On Bicycle: " + activity.getConfidence() );
                    break;

                }
                case DetectedActivity.ON_FOOT: {
                    Log.e( "ActivityRecogition", "On Foot: " + activity.getConfidence() );
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
                    final String act = "You Are Running";
                    currentActivity = ActivitiesEnum.RUNNING.toString();
                    break;

                }
                case DetectedActivity.STILL: {
                    Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );
                    final String act = "You Are Still";
                    currentActivity = ActivitiesEnum.STILL.toString();
                    break;

                }
                case DetectedActivity.TILTING: {
                    Log.e( "ActivityRecogition", "Tilting: " + activity.getConfidence() );
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                        builder.setContentText( "Are you walking?" );
                        builder.setSmallIcon( R.mipmap.ic_launcher );
                        builder.setContentTitle( getString( R.string.app_name ) );
                        NotificationManagerCompat.from(this).notify(0, builder.build());
                    }
                    final String act = "You Are Walking";
                    currentActivity = ActivitiesEnum.WALKING.toString();
                    break;

                }
                case DetectedActivity.UNKNOWN: {
                    Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );
                    currentActivity = ActivitiesEnum.UNKNOWN.toString();
                    break;

                }
            }
        }

        return currentActivity;
    }
}
