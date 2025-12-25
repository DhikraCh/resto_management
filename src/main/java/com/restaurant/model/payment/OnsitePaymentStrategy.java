package com.restaurant.model.payment;

/**
 * PATRON STRATÉGIE - Stratégie concrète pour paiement sur place
 */
public class OnsitePaymentStrategy implements PaymentStrategy {

    @Override
    public boolean pay(double amount) {
        System.out.println("🏪 Paiement de " + amount + " DA sur place effectué avec succès");
        return true;
    }

    @Override
    public String getPaymentMethod() {
        return "Paiement sur place";
    }
}