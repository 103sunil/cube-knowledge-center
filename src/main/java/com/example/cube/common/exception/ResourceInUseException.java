package com.example.cube.common.exception;

/** Thrown when deleting a row would orphan references that would normally
 *  be blocked by a FOREIGN KEY constraint - enforced here in code instead
 *  since this schema has none, per company policy. */
public class ResourceInUseException extends RuntimeException {
    public ResourceInUseException(String message) {
        super(message);
    }
}