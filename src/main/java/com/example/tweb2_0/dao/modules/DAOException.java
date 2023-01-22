package com.example.tweb2_0.dao.modules;

import java.sql.SQLException;

public class DAOException extends Exception{
    public final static int _FAIL_TO_INSERT = 1;
    public final static int _UPDATE_FAILED = 2;
    public  final static int _SQL_ERROR = 3;
    public final static int _FAIL_TO_DELETE = 4;
    public final static int _WAIT_FOR_A_MINUTE = 5;

    public String message;

    private SQLException sqlException;

    private int errorCode;

    public DAOException(int errorCode){
        this.errorCode = errorCode;
        switch (errorCode){
            case _WAIT_FOR_A_MINUTE:
                this.message = "_WAIT_FOR_A_MINUTE";
                break;
            case _FAIL_TO_DELETE:
                this.message = "_FAIL_TO_DELETE";
                break;
            case _FAIL_TO_INSERT:
                this.message = "_FAIL_TO_INSERT";
                break;
            default:
                this.message = "Error";
        }
    }

    public DAOException(SQLException e){
        this.sqlException = e;
        if(sqlException.getErrorCode() == 1062){
            this.errorCode = _FAIL_TO_INSERT;
        }
        else this.errorCode = sqlException.getErrorCode();
        this.message = e.getMessage();
    }

    public int getErrorCode() {
        return this.errorCode;

    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
