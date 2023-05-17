/*
 * PROPRIETARY/CONFIDENTIAL
 */
package com.azazar.sqldumpparser;

/**
 *
 * @author Mikhail Yevchenko <spam@uo1.net>
 * @since  May 17, 2023
 */
public interface SqlValue extends SqlToken {
    
    Object getValue();

}
