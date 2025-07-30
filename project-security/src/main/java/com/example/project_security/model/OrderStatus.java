package com.example.project_security.model;

/**
 * Enum che rappresenta i possibili stati di un ordine nel sistema e-commerce.
 * Definisce il ciclo di vita di un ordine dal momento della creazione alla consegna.
 */
public enum OrderStatus {
    /**
     * Ordine creato ma pagamento non ancora confermato
     */
    PENDING("In attesa di pagamento"),
    
    /**
     * Pagamento ricevuto e confermato
     */
    PAYMENT_CONFIRMED("Pagamento confermato"),
    
    /**
     * Ordine in fase di preparazione
     */
    PROCESSING("In lavorazione"),
    
    /**
     * Ordine spedito
     */
    SHIPPED("Spedito"),
    
    /**
     * Ordine consegnato al cliente
     */
    DELIVERED("Consegnato"),
    
    /**
     * Ordine cancellato
     */
    CANCELLED("Cancellato"),
    
    /**
     * Ordine rimborsato
     */
    REFUNDED("Rimborsato"),
    
    /**
     * Ordine restituito
     */
    RETURNED("Reso");
    
    private final String description;
    
    OrderStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Verifica se lo stato corrente può transizionare verso un nuovo stato
     */
    public boolean canTransitionTo(OrderStatus newStatus) {
        switch (this) {
            case PENDING:
                return newStatus == PAYMENT_CONFIRMED || newStatus == CANCELLED;
            case PAYMENT_CONFIRMED:
                return newStatus == PROCESSING || newStatus == CANCELLED;
            case PROCESSING:
                return newStatus == SHIPPED || newStatus == CANCELLED;
            case SHIPPED:
                return newStatus == DELIVERED || newStatus == RETURNED;
            case DELIVERED:
                return newStatus == RETURNED || newStatus == REFUNDED;
            case CANCELLED:
            case REFUNDED:
            case RETURNED:
                return false; // Stati finali
            default:
                return false;
        }
    }
}