package cn.nicolite.huthelper.view.presenter;

import android.Manifest;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

import cn.nicolite.huthelper.base.BasePresenter;
import cn.nicolite.huthelper.db.dao.UserDao;
import cn.nicolite.huthelper.model.bean.HttpResult;
import cn.nicolite.huthelper.model.bean.UploadImages;
import cn.nicolite.huthelper.model.bean.User;
import cn.nicolite.huthelper.network.APIUtils;
import cn.nicolite.huthelper.network.exception.ExceptionEngine;
import cn.nicolite.huthelper.view.activity.UserInfoActivity;
import cn.nicolite.huthelper.view.iview.IUserInfoView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * UserInfoPresenter
 * Created by nicolite on 17-10-28.
 */

public class UserInfoPresenter extends BasePresenter<IUserInfoView, UserInfoActivity> {
    public UserInfoPresenter(IUserInfoView view, UserInfoActivity activity) {
        super(view, activity);
    }

    public void showUserData() {

        User user = configure.getUser();

        if (user == null) {
            if (getView() != null) {
                getView().showMessage("获取当前登录用户失败，请重新登录！");
            }
            return;
        }
        getView().showUserInfo(user);
    }

    public void uploadAvatar(final Bitmap bitmap) {

        if (getView() != null) {
            getView().showMessage("头像上传中！");
        }

        APIUtils
                .getUploadAPI()
                .uploadImages()
                .compose(getActivity().<UploadImages>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UploadImages>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getActivity().showLoading();
                    }

                    @Override
                    public void onNext(UploadImages uploadImages) {
                        if (getView() != null) {
                            getView().closeLoading();
                            String msg = "修改失败！";
                            if (uploadImages.getCode() == 200) {
                                msg = "修改成功!";
                                User user = configure.getUser();
                                user.setHead_pic_thumb(uploadImages.getData());
                                user.setHead_pic(uploadImages.getData());
                                daoSession.getUserDao().update(user);
                                getView().changeAvatarSuccess(bitmap);

                            }
                            getView().showMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getView() != null) {
                            getView().closeLoading();
                            getView().showMessage(ExceptionEngine.handleException(e).getMsg());
                        }

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void changAvatar() {
        AndPermission
                .with(getActivity())
                .requestCode(100)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                        if (getView() != null) {
                            if (requestCode == 100) {
                                getView().changeAvatar();
                            }
                        }
                    }

                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                        if (getView() != null) {
                            getView().showMessage("获取权限失败，请授予文件读写权限！");
                        }
                    }
                })
                .start();
    }

    public void changeUserName(final String userName) {

        if (getView() != null) {
            getView().showMessage("昵称修改中！");
        }

        APIUtils
                .getUserAPI()
                .changeUsername()
                .compose(getActivity().<HttpResult>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult httpResult) {
                        if (getView() != null) {
                            if (httpResult.getCode() == 200) {
                                UserDao userDao = daoSession.getUserDao();
                                User user = configure.getUser();
                                user.setUsername(userName);
                                userDao.update(user);

                                getView().showMessage("修改成功!");
                            } else {
                                getView().showMessage("修改失败，code：" + httpResult.getCode()
                                        + "，" + httpResult.getMsg());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getView() != null) {
                            getView().showMessage(ExceptionEngine.handleException(e).getMsg());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //更换个人签名
    public void changeBio(final String bio) {

        APIUtils
                .getUserAPI()
                .changeBio()
                .compose(getActivity().<HttpResult>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult httpResult) {
                        if (getView() != null) {
                            if (httpResult.getCode() == 200) {
                                UserDao userDao = daoSession.getUserDao();
                                User user = configure.getUser();
                                user.setBio(bio);
                                userDao.update(user);

                                getView().showMessage("修改成功!");
                            } else {
                                getView().showMessage("修改失败，" + httpResult.getCode());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getView() != null) {
                            getView().showMessage(ExceptionEngine.handleException(e).getMsg());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
