package youtubedownloadhelper.download;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import youtubedownloadhelper.R;
import youtubedownloadhelper.Preferences.SharePerferenceHelper;

/**
 * Created by andy on 2015/2/27.
 */
public class DownloadDialog extends AlertDialog.Builder {

    private ImageView bt_addDirc;
    private ListView listview;
    private ImageView iv_back;
    private EditText et_name;
    private TextView tv_path;
    private FileAdapter fileAdapter;
    private String fileName;
    private Context context;
    private File currentFile;

    private  List<File> fileList = new ArrayList<File>();
    private OnFileSelectListener onFileSelectListener;

    public interface OnFileSelectListener{
        void onFileSelected(String filePah);
    }

    public String getCurrentFilePath() {
        return currentFile.getAbsolutePath();
    }
    public String getCurrentFileName(){
        return et_name.getText().toString();
    }


    public void setOnFileSelectListener(OnFileSelectListener onFileSelectListener) {
        this.onFileSelectListener = onFileSelectListener;
    }

    public DownloadDialog(final Context context, String fileName) {
        super(context);
        this.fileName = fileName;
        this.context = context;
        init(context);
    }


    public void init(final Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_file_selecter,null);
        bt_addDirc = (ImageView) view.findViewById(R.id.button2);
        listview = (ListView) view.findViewById(R.id.listView2);
        iv_back = (ImageView) view.findViewById(R.id.imageView3);
        et_name = (EditText) view.findViewById(R.id.fileName);
        tv_path = (TextView) view.findViewById(R.id.path);
        setTitle(R.string.select_dircs);
        fileAdapter = new FileAdapter(context,fileList);
        listview.setAdapter(fileAdapter);
        setView(view);
        et_name.setText(fileName);
        iv_back.setOnClickListener(new BackEvent());
        String path =  SharePerferenceHelper.getInstance(context).getString("path",Environment.getExternalStorageDirectory().getAbsolutePath());
        String curPath = fileAdapter.updateFileAndreturnCurPath(new File(path));
        tv_path.setText(curPath);
        bt_addDirc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ab = new AlertDialog.Builder(context);
                ab.setTitle(R.string.add_dirctory);
                final EditText et =new EditText(context);
                et.setTextColor(Color.BLUE);
                et.setGravity(Gravity.CENTER);
                et.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
                et.setText(R.string.new_dir);
                ab.setView(et);
                ab.setPositiveButton(R.string.alert_ok,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String dirName = et.getText().toString();
                        if(createNewDir(currentFile.getAbsolutePath(),dirName)){
                            String curPath = fileAdapter.updateFileAndreturnCurPath(new File(currentFile.getAbsolutePath(),dirName));
                            tv_path.setText(curPath);
                        }
                        dialog.cancel();
                    }
                });
                ab.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
                ab.create().show();
            }
        });
        setPositiveButton(R.string.Alert_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               if(onFileSelectListener != null)
                   onFileSelectListener.onFileSelected(getCurrentFilePath());
            }
        });
        setNegativeButton(R.string.alert_cancel, null);
        setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                SharePerferenceHelper.getInstance(context).setString("path",currentFile.getAbsolutePath());

            }
        });

    }


    @Override
    public AlertDialog.Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        return null;
    }

    @Override
    public AlertDialog.Builder setNegativeButton(int textId, DialogInterface.OnClickListener listener) {
        return null;
    }

    private  boolean createNewDir(String dstPath, String dirName){
        File desfile = new File(dstPath);
        String[] filelist = desfile.list();
        int count=0;
        for(int i=0;i<filelist.length;i++){
            if(dirName.equals(filelist[i])){
                count++;
            }
        }
        if(count>0){
            dirName+="("+count+")";
        }
        File newdesfile = new File(dstPath,dirName);
        if(!newdesfile.exists()){
            if(newdesfile.mkdirs()){
                return true;
            }
        }
        return false;
    }


    private class BackEvent implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(currentFile!=null) {
                if(currentFile.getParent()!=null){
                    fileAdapter.updateFileAndreturnCurPath(new File(currentFile.getParent()));
                }

            }
        }
    }

     private class FileAdapter extends BaseAdapter {
        private List<File> data;
        private Context context;

        public FileAdapter(Context context, List<File> objects) {
            data = objects;
            this.context = context;
        }

        @Override
        public int getCount() {
            if(data!=null)
                return data.size();
            return 0;
        }

        @Override
        public File getItem(int position) {
            if(data!=null){
                if(position<data.size())
                    return data.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        private class ViewHolder{
            TextView tv;
            ImageView iv;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if(convertView==null){
                vh = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.fileitem,null);
                vh.tv = (TextView) convertView.findViewById(R.id.textView4);
                vh.iv = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(vh);
            }else{
                vh = (ViewHolder) convertView.getTag();
            }


            File file = getItem(position);
            vh.tv.setText(file.getName());
            vh.iv.setVisibility(file.isDirectory()?View.VISIBLE:View.INVISIBLE);
            convertView.setOnClickListener(new View.OnClickListener(){
                                               @Override
                                               public void onClick(View v) {
                                                   File f = data.get(position);
                                                   if(f.isDirectory()){
                                                       updateFileAndreturnCurPath(f);
                                                   }
                                               }
                                           }
            );
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    new AlertDialog.Builder(context)
                            .setTitle(R.string.delete_tip)
                            .setNegativeButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    File f = data.get(position);
                                    if(f.exists()){
                                        f.delete();
                                    }
                                    updateFileAndreturnCurPath(currentFile);
                                }
                            })
                            .setPositiveButton(R.string.alert_cancel,null)
                            .create().show();
                    return false;
                }
            });
            return convertView;
        }

        private String updateFileAndreturnCurPath(File file){

            data.clear();
            if(file.exists()&&file.isDirectory()){
                currentFile = file;
            }else{
                currentFile = Environment.getExternalStorageDirectory();
            }
            File[] dirs = file.listFiles();
            if(dirs!=null)
                for(File item : dirs){
                    if(item.canRead()&&item.canWrite()&&!item.getName().startsWith("."))
                        data.add(item);
                }
            Collections.sort(data ,new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    int index1 = lhs.isDirectory()?0:1;
                    int index2 = rhs.isDirectory()?0:1;
                    return index1 - index2;
                }
            });
            notifyDataSetChanged();
            return  currentFile.getAbsolutePath();
        }


    }

}
