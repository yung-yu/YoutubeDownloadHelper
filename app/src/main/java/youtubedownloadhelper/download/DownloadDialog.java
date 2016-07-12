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
import youtubedownloadhelper.Preferences.sharePerferenceHelper;

/**
 * Created by andy on 2015/2/27.
 */
public class DownloadDialog extends AlertDialog.Builder {

    ImageView bt_addDirc;
    ListView listview;
    ImageView iv_back;
    EditText et_name;
    TextView tv_path;
    FileAdapter fileAdapter;

    public String getCurrentFilePath() {
        return currentFile.getAbsolutePath();
    }
    public String getCurrentFileName(){
        return et_name.getText().toString();
    }
    File currentFile ;
    List<File> fileList = new ArrayList<File>();
    String fileName;
    Context context;
    public DownloadDialog(final Context context,String fileName) {
        super(context);
        this.fileName = fileName;
        this.context = context;
        setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                sharePerferenceHelper.getIntent(context).setString("path",currentFile.getAbsolutePath());

            }
        });

        init(context);
    }


    public void init(final Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.choosedirctorydialog,null);
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
        String path =  sharePerferenceHelper.getIntent(context).getString("path",Environment.getExternalStorageDirectory().getAbsolutePath());
        updateFile(new File(path));
        bt_addDirc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ab = new AlertDialog.Builder(context);
                ab.setTitle(R.string.add_dirctory);
                final EditText et =new EditText(context);
                et.setTextColor(Color.BLUE);
                et.setGravity(Gravity.CENTER);
                et.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
                et.setText("新資料夾");
                ab.setView(et);
                ab.setPositiveButton(R.string.alert_ok,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String dirName = et.getText().toString();
                        if(createNewDir(currentFile.getAbsolutePath(),dirName)){
                            updateFile(new File(currentFile.getAbsolutePath(),dirName));
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
    }

    public  boolean createNewDir(String dstPath,String dirName){
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

    public void updateFile(File file){

        fileList.clear();
        if(file.exists()&&file.isDirectory()){
            currentFile = file;
        }else{
            currentFile = Environment.getExternalStorageDirectory();
        }
        tv_path.setText(currentFile.getAbsolutePath());
        File[] dirs = file.listFiles();
        if(dirs!=null)
            for(File item : dirs){
                if(item.canRead()&&item.canWrite()&&!item.getName().startsWith("."))
                    fileList.add(item);
            }
        Collections.sort(fileList ,new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                int index1 = lhs.isDirectory()?0:1;
                int index2 = rhs.isDirectory()?0:1;
                return index1 - index2;
            }
        });
        fileAdapter.notifyDataSetChanged();
    }
    public class BackEvent implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(currentFile!=null) {
                if(currentFile.getParent()!=null){
                        updateFile(new File(currentFile.getParent()));
                }

            }
        }
    }
    private class FileAdapter extends BaseAdapter {
        List<File> data;
        Context context;
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
                vh = new ViewHolder();;
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
                           updateFile(f);
                       }
                   }
               }
            );
            return convertView;
        }
    }
}
