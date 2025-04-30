package com.gmwapp.hi_dude.BillingManager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import com.gmwapp.hi_dude.BaseApplication;

import com.android.billingclient.api.*;
import com.gmwapp.hi_dude.retrofit.responses.UserData;
import com.gmwapp.hi_dude.utils.DPreferences;
import com.gmwapp.hi_dude.viewmodels.WalletViewModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BillingManager {
    private final BillingClient billingClient;
    private final Activity activity;
    private List<SkuDetails> skuDetailsList;
    private final Set<String> handledPurchaseTokens = new HashSet<>();


    public BillingManager(Activity activity) {
        this.activity = activity;
        billingClient = BillingClient.newBuilder(activity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        startBillingConnection();
    }

    private void startBillingConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d("Billing", "Billing Client is ready!");
                    queryAvailableProducts();
                } else {
                    Log.e("Billing", "Billing setup failed: " + billingResult.getResponseCode());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.e("Billing", "Billing service disconnected. Retrying...");
                startBillingConnection();
            }
        });
    }

    private void queryAvailableProducts() {
//        List<String> skuList = List.of("coins_11", "coins_12", "2", "3", "4", "5", "6", "7", "8", "9"); // Ensure these match Play Console

        DPreferences preferences = new DPreferences(activity);
        List<String> skuList = preferences.getSkuList();
        Log.d("skuListChecks", String.valueOf(skuList));
        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)  // Use .SkuType.SUBS for subscriptions
                .build();

        billingClient.querySkuDetailsAsync(params, (billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                this.skuDetailsList = skuDetailsList;

                if (skuDetailsList.isEmpty()) {
                    Log.e("Billing", "No products found in Google Play Console.");
                } else {
                    for (SkuDetails skuDetails : skuDetailsList) {
                        Log.d("Billing", "Product Found: " + skuDetails.getSku() + " Price: " + skuDetails.getPrice());
                    }
                }
            } else {
                Log.e("Billing", "Failed to fetch products: " + billingResult.getResponseCode());
            }
        });
    }

    public void purchaseProduct(String productId) {
        if (skuDetailsList == null || skuDetailsList.isEmpty()) {
            Toast.makeText(activity, "No products available!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("productId",productId);

        SkuDetails selectedProduct = null;
        for (SkuDetails sku : skuDetailsList) {
            if (sku.getSku().equals(productId)) {
                selectedProduct = sku;
                break;
            }
        }

        if (selectedProduct == null) {
            Toast.makeText(activity, "Product not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        BillingFlowParams params = BillingFlowParams.newBuilder()
                .setSkuDetails(selectedProduct)
                .build();

        billingClient.launchBillingFlow(activity, params);
    }

    private final PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    if (!purchase.isAcknowledged()) {
                        handlePurchase(purchase);
                    }
                }
            }
        } else {
            Log.e("Billing", "Purchase failed or cancelled: " + billingResult.getResponseCode());
        }
    };

    private void handlePurchase(Purchase purchase) {
        String purchaseToken = purchase.getPurchaseToken();
        String playConsoleOrder = purchase.getOrderId();

        if (playConsoleOrder == null || playConsoleOrder.isEmpty()) {
            Log.e("Billing", "Order ID is null or empty. Stopping execution.");
            return;
        }

        if (handledPurchaseTokens.contains(purchaseToken)) {
            Log.d("Billing", "Purchase already handled: " + purchaseToken);
            return;
        }



        handledPurchaseTokens.add(purchaseToken);
        Log.d("Billing", "Handling purchase: " + purchaseToken);

        DPreferences preferences = new DPreferences(activity);
        String savedUserId = preferences.getSelectedUserId();
        String savedCoinId = preferences.getSelectedPlanId();
        String savedOrderId = preferences.getSelectedOrderId();

        if (savedUserId.equals("0") || savedCoinId.equals("0") || savedOrderId.equals("0")) {
            Log.e("Billing", "Invalid saved userId or coinId or orderId");
            return;
        }

        int user_id = Integer.parseInt(savedUserId);
        int coin_id = Integer.parseInt(savedCoinId);
        int order_id = Integer.parseInt(savedOrderId);

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            ConsumeParams consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchaseToken)
                    .build();

            billingClient.consumeAsync(consumeParams, (billingResult, token) -> {
                WalletViewModel walletViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(WalletViewModel.class);

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d("Billing", "Product consumed successfully!");
                    try {
                        Log.d("Billing", "Calling addCoins with userId=" + user_id + ", coinId=" + coin_id);
                        walletViewModel.addCoins(user_id, coin_id, 1, order_id, "Coins purchased");

                        activity.runOnUiThread(() ->
                                Toast.makeText(activity, "Coins purchased successfully", Toast.LENGTH_SHORT).show()
                        );
                    } catch (Exception e) {
                        Log.e("Billing", "Calling tryCoins with userId=" + user_id + ", coinId=" + coin_id);
                        walletViewModel.tryCoins(user_id, coin_id, 2, order_id, "Error: " + e.getMessage());
                        Log.e("Billing", "Error updating coins: " + e.getMessage());
                        activity.runOnUiThread(() ->
                                Toast.makeText(activity, "Billing Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    }
//                } else {
//                    Log.e("Billing", "Failed to consume purchase: " + billingResult.getResponseCode());
//                    walletViewModel.tryCoins(user_id, coin_id, 2, order_id, "Coins purchase failed " + billingResult.getResponseCode());
//
//                    activity.runOnUiThread(() ->
//                            Toast.makeText(activity, "Purchase saved temporarily, will retry", Toast.LENGTH_SHORT).show()
//                    );
                }
            });
        }
    }


}
