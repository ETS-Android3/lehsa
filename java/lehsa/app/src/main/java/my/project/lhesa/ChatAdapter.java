package my.project.lhesa;

/**
 * Created by VMac on 17/11/16.
 */

//import android.graphics.Typeface;
//import android.support.v7.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private int SELF = 100;
    private ArrayList<Message> messageArrayList;


    public ChatAdapter(ArrayList<Message> messageArrayList) {
        this.messageArrayList=messageArrayList;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        // view type is to identify where to render the chat message
        // left or right
        if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self, parent, false);
        } else {
            // WatBot message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_watson, parent, false);
        }


        return new ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        if (message.getId()!=null && message.getId().equals("1")) {
            return SELF;
        }

        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        String REGEX = "(?!<a[^>]*?>)(http[^\\s]+)(?![^<]*?</a>)";
        Message message = messageArrayList.get(position);
        message.setMessage(message.getMessage());
        String r = message.getMessage();
        String msg = r.replaceAll("\\\\n", "\n");
        msg = msg.replaceAll("\\\\", "");
        ((ViewHolder) holder).message.setText(msg);
        ((ViewHolder) holder).message.setText(msg);
        ((ViewHolder) holder).message.setMovementMethod(new ScrollingMovementMethod());
        Linkify.addLinks(((ViewHolder) holder).message, Linkify.WEB_URLS);
        Linkify.addLinks(((ViewHolder) holder).message, Linkify.EMAIL_ADDRESSES);
        Linkify.addLinks(((ViewHolder) holder).message, Pattern.compile(REGEX), "https://");
        ((ViewHolder) holder).message.setLinksClickable(true);
        }

    @Override
    public int getItemCount() {
            return messageArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        public ViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.message);

            //TODO: Uncomment this if you want to use a custom Font
            /*String customFont = "Montserrat-Regular.ttf";
            Typeface typeface = Typeface.createFromAsset(itemView.getContext().getAssets(), customFont);
            message.setTypeface(typeface);*/

        }
    }


}