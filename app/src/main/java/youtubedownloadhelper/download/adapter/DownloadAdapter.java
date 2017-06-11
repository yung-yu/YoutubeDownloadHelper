package youtubedownloadhelper.download.adapter;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import youtubedownloadhelper.R;
import youtubedownloadhelper.object.Video;
import youtubedownloadhelper.download.DownloadDialog;
import youtubedownloadhelper.download.DownloadTask;
import youtubedownloadhelper.download.adapter.viewholder.MyViewHolder;
import youtubedownloadhelper.mp3.Mp3Helper;
import youtubedownloadhelper.utils.AndroidUtils;
import youtubedownloadhelper.youtube.YotubeItag;

public class DownloadAdapter extends BaseAdapter {
	private List<Video> list;
	private ProgressDialog mProgressDialog;
	private Context context;

	private Map<Integer, DownloadTask> taskCache = new HashMap<>();
	private FFmpeg ffmpeg ;


	public DownloadAdapter(Context context) {
		this.context = context;
	}

	public void setList(List<Video> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		return list != null ? list.size() : 0;
	}

	public Video getItem(int position) {
		return list != null && position < list.size() ? list.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void putDownloadCache(int position, DownloadTask task) {
		taskCache.put(position, task);
	}
	public void removeDownloadCache(int positon) {
		taskCache.put(positon, null);
	}
	public boolean isDownloading(int position) {
		return taskCache.get(position) != null && !taskCache.get(position).isCancelled();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		MyViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.downloaditem, null);
			holder = new MyViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (MyViewHolder) convertView.getTag();
		}
		Video video = getItem(position);
		holder.mp3Button.setVisibility(View.GONE);
		if (holder != null && video != null) {
			holder.setVideo(video);

			DownloadTask task = taskCache.get(position);
			if (task != null && !task.isCancelled()) {
				holder.bt.setVisibility(View.VISIBLE);

				holder.bt.setTag(task);
				holder.bt.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						DownloadTask task = (DownloadTask) v.getTag();
						if (task != null && !task.isCancelled()) {
							task.cancel(true);
							taskCache.remove(task);
							notifyDataSetChanged();
						}
					}
				});
				holder.progressBar.setVisibility(View.VISIBLE);
				holder.progressBar.setProgress(task.curProgress);
				holder.text.setText(YotubeItag.getVideoDescribe(video.getItag())
						+"-("+task.curProgress+"/100)");
			} else {
				holder.bt.setVisibility(View.GONE);
				holder.progressBar.setVisibility(View.GONE);
				holder.text.setText(YotubeItag.getVideoDescribe(video.getItag())
						+(video.isDownlaod()?"-播放":""));
				if(video.isDownlaod()) {
					holder.mp3Button.setVisibility(View.VISIBLE);
					holder.mp3Button.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							final Video video = getItem(position);
							final File file = new File(video.getLocalFilePath());
							String name = file.getName();
                            name = name.substring(0, name.lastIndexOf("."));
							final DownloadDialog downloadDialog = new DownloadDialog(context, name + ".mp3");
							downloadDialog.setOnFileSelectListener(new DownloadDialog.OnFileSelectListener() {
								@Override
								public void onFileSelected(final String filePah) {
									ffmpeg = Mp3Helper.getInstance().covertToMp3(context, file.getPath(), filePah + "/" + downloadDialog.getCurrentFileName(), new Mp3Helper.OnCovertListener() {
										@Override
										public void onStart() {
											if (mProgressDialog == null) {
												mProgressDialog = new ProgressDialog(context);
												mProgressDialog.setCancelable(false);
												mProgressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
													@Override
													public void onClick(DialogInterface dialog, int which) {
														if(ffmpeg != null){
															ffmpeg.killRunningProcesses();
														}
														dialog.cancel();
													}
												});
												mProgressDialog.setMessage(context.getString(R.string.converting_mp3));
											}
											mProgressDialog.show();

										}

										@Override
										public void onSuccess(final String path) {
											if (mProgressDialog != null) {
												mProgressDialog.dismiss();
											}
											new AlertDialog.Builder(context)
													.setMessage(R.string.mp3_success)
													.setPositiveButton(R.string.Alert_ok, new DialogInterface.OnClickListener() {
														@Override
														public void onClick(DialogInterface dialog, int which) {
															AndroidUtils.playMusic(context, path);
															dialog.cancel();
														}
													})
													.setNegativeButton(R.string.alert_cancel, null)
													.create().show();


										}

										@Override
										public void onFailed(String msg) {
											if (mProgressDialog != null) {
												mProgressDialog.dismiss();
											}
											Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

										}

										@Override
										public void onProgress(String msg) {
											if (mProgressDialog != null) {
												mProgressDialog.setMessage(msg);
											}
										}

									});
								}
							});
							downloadDialog.create().show();
						}
					});
				}

			}
		}
		return convertView;
	}
}
