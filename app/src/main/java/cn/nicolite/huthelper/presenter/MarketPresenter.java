package cn.nicolite.huthelper.presenter;

import java.util.List;

import cn.nicolite.huthelper.base.presenter.BasePresenter;
import cn.nicolite.huthelper.model.bean.Goods;
import cn.nicolite.huthelper.model.bean.GoodsResult;
import cn.nicolite.huthelper.network.api.APIUtils;
import cn.nicolite.huthelper.network.exception.ExceptionEngine;
import cn.nicolite.huthelper.utils.ListUtils;
import cn.nicolite.huthelper.view.fragment.MarketFragment;
import cn.nicolite.huthelper.view.iview.IMarketView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * MarketPresenter
 * Created by nicolite on 17-11-6.
 */

public class MarketPresenter extends BasePresenter<IMarketView, MarketFragment> {

    public static final int All = 0;
    public static final int SOLD = 1;
    public static final int BUY = 2;

    public MarketPresenter(IMarketView view, MarketFragment activity) {
        super(view, activity);
    }


    public void showGoodsList(int type, boolean isManual) {
        switch (type) {
            case All:
                loadMoreAll(1, isManual, false);
                break;
            case SOLD:
                loadMoreSold(1, isManual, false);
                break;
            case BUY:
                loadMoreBuy(1, isManual, false);
                break;
        }
    }

    public void loadMore(int page, int type) {
        switch (type) {
            case All:
                loadMoreAll(page, true, true);
                break;
            case SOLD:
                loadMoreSold(page, true, true);
                break;
            case BUY:
                loadMoreBuy(page, true, true);
                break;
        }
    }

    public void loadMoreAll(int page, boolean isManual, boolean isLoadMore) {
        loadGoodsList(page, "", isManual, isLoadMore);
    }

    public void loadMoreSold(int page, boolean isManual, boolean isLoadMore) {
        loadGoodsList(page, "1", isManual, isLoadMore);
    }

    public void loadMoreBuy(int page, boolean isManual, boolean isLoadMore) {
        loadGoodsList(page, "2", isManual, isLoadMore);
    }


    public void loadGoodsList(final int page, String type, final boolean isManual, final boolean isloadMore) {

        APIUtils
                .getMarketAPI()
                .getGoodsList(page, type)
                .compose(getActivity().<GoodsResult<List<Goods>>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GoodsResult<List<Goods>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (getView() != null && !isManual) {
                            getView().showLoading();
                        }
                    }

                    @Override
                    public void onNext(GoodsResult<List<Goods>> listGoodsResult) {
                        if (getView() != null) {
                            getView().closeLoading();
                            if (listGoodsResult.getCode() == 200 && !ListUtils.isEmpty(listGoodsResult.getData())) {
                                if (isloadMore) {
                                    if (page <= listGoodsResult.getPageination()) {
                                        getView().showLoadMoreList(listGoodsResult.getData());
                                    } else {
                                        getView().noMoreData();
                                    }
                                } else {
                                    getView().showGoodsList(listGoodsResult.getData());
                                }
                            } else {
                                getView().showMessage("获取数据失败，" + listGoodsResult.getCode());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getView() != null) {
                            getView().closeLoading();
                            getView().loadMoreFailure();
                            getView().showMessage(ExceptionEngine.handleException(e).getMsg());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}