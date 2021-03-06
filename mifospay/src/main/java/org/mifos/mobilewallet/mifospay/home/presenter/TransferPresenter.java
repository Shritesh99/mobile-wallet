package org.mifos.mobilewallet.mifospay.home.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount;
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientData;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.home.HomeContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import javax.inject.Inject;

/**
 * Created by naman on 30/8/17.
 */

public class TransferPresenter implements HomeContract.TransferPresenter {

    private final UseCaseHandler mUsecaseHandler;
    private final LocalRepository localRepository;
    @Inject
    FetchClientData fetchClientData;
    @Inject
    FetchAccount mFetchAccount;

    private HomeContract.TransferView mTransferView;

    @Inject
    public TransferPresenter(UseCaseHandler useCaseHandler, LocalRepository localRepository) {
        this.mUsecaseHandler = useCaseHandler;
        this.localRepository = localRepository;
    }

    @Override
    public void attachView(BaseView baseView) {
        mTransferView = (HomeContract.TransferView) baseView;
        mTransferView.setPresenter(this);
    }

    @Override
    public void fetchVpa() {
        mTransferView.showVpa(localRepository.getClientDetails().getExternalId());
    }

    @Override
    public void fetchMobile() {
        mTransferView.showMobile(localRepository.getPreferencesHelper().getMobile());
    }

    @Override
    public void checkBalanceAvailability(final String externalId, final double transferAmount) {
        mUsecaseHandler.execute(mFetchAccount,
                new FetchAccount.RequestValues(localRepository.getClientDetails().getClientId()),
                new UseCase.UseCaseCallback<FetchAccount.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchAccount.ResponseValue response) {
                        mTransferView.hideSwipeProgress();
                        if (transferAmount > response.getAccount().getBalance()) {
                            mTransferView.showSnackbar(Constants.INSUFFICIENT_BALANCE);
                        } else {
                            mTransferView.showClientDetails(externalId, transferAmount);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        mTransferView.hideSwipeProgress();
                        mTransferView.showToast(Constants.ERROR_FETCHING_BALANCE);
                    }
                });
    }
}
