package idhub.sortinparcels.exceptions;

public class ParcelNotFoundException extends RuntimeException {
    public ParcelNotFoundException(String trackingNumber) {
        super("Parcel with tracking number '" + trackingNumber + "' not found");
    }
}
