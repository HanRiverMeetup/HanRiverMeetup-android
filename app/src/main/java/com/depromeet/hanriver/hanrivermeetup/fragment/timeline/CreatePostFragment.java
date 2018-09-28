package com.depromeet.hanriver.hanrivermeetup.fragment.timeline;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.depromeet.hanriver.hanrivermeetup.R;
import com.depromeet.hanriver.hanrivermeetup.common.PreferencesManager;
import com.depromeet.hanriver.hanrivermeetup.fragment.meeting.Utils.CreateRoomLocationFragment;
import com.depromeet.hanriver.hanrivermeetup.model.timeline.TimeLineVO;
import com.depromeet.hanriver.hanrivermeetup.service.TimelineService;

import java.io.IOException;

import javax.net.ssl.HttpsURLConnection;

import butterknife.OnClick;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

public class CreatePostFragment extends DialogFragment {
    private String currentPhotoPath;//실제 사진 파일 경로
    private final int CAMERA_CODE = 1111;
    private final int GALLERY_CODE = 1112;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @BindView(R.id.create_post_location) TextView locationTextView;
    @BindView(R.id.create_post_content) TextView contentTextView;
    @BindView(R.id.image) ImageView imageView;

    @OnClick(R.id.create_post_back_btn)
    public void backFragment() {
        this.dismiss();
    }

    @OnClick(R.id.add_image_btn)
    public void setImage() {
        ImageSheetDialogFragment imageSheetDialogFragment = new ImageSheetDialogFragment();
        imageSheetDialogFragment.show(getFragmentManager(), "ImageSheet");
    }

    @OnClick(R.id.set_location_ll)
    public void setLocation() {
        CreateRoomLocationFragment dialog = CreateRoomLocationFragment.newInstance(locationTextView);
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
        dialog.show(getFragmentManager(), "tag");
    }

    @OnClick(R.id.create_post_btn)
    public void createPost() {
        if (!isValidate()) {
            Toast.makeText(getContext(), "입력 값을 확인해주세요!", Toast.LENGTH_SHORT).show();
            return;
        }

        pushImage();


    }

    private void pushImage() {
        pushPost();
    }

    private void pushPost() {
        mCompositeDisposable.add(TimelineService.getInstance().createPost(createPostData())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(res -> {
                    if (res.code() == HttpsURLConnection.HTTP_OK) {
                        this.dismiss();
                    } else {
                        Toast.makeText(getContext(), "해당 날짜에 이미 방을 만들었습니다.", Toast.LENGTH_SHORT).show();
                        this.dismiss();
                    }
                })
                .subscribe());
    }

    private TimeLineVO createPostData() {
        TimeLineVO post = new TimeLineVO();
        post.content = contentTextView.getText().toString();
        post.location = locationTextView.getText().toString();
        post.user_id = PreferencesManager.getUserID();
        post.nickname = PreferencesManager.getNickname();
        post.imageurl = "https://cdn.pixabay.com/photo/2015/04/19/08/32/marguerite-729510_1280.jpg";

        return post;
    }

    private boolean isValidate() {
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null)
            return;

        getDialog().getWindow().setWindowAnimations(
                R.style.dialog_animation_move_to_up);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public static CreatePostFragment newInstance() {
        CreatePostFragment fragment = new CreatePostFragment();
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_CODE:
                    sendPicture(data.getData());
                    break;
                case CAMERA_CODE:
                    getPictureForPhoto();
                    break;
                default:
                    break;
            }
        }
    }

    private void sendPicture(Uri imgUri) {

        String imagePath = getRealPathFromURI(imgUri); // path 경로
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
        imageView.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기

    }

    private void getPictureForPhoto() {
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;

        if (exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientationToDegrees(exifOrientation);
        } else {
            exifDegree = 0;
        }
        imageView.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap src, float degree) {

        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }
}
