package com.somrani.nader.projetprocessmaker.interfaces;

public class DraftProcessItem {
    public String app_uid;
    public String tas_uid;
    public String pro_uid;
    public String app_pro_title;
    public String app_tas_title;
    public String app_create_date;
    public String app_update_date;
    public String usr_firstname;
    public String usr_lastname;
    public String usr_username;
    public String del_task_due_date;


    public DraftProcessItem(String app_uid, String tas_uid, String pro_uid, String app_pro_title, String app_tas_title, String app_create_date, String app_update_date, String usr_firstname, String usr_lastname, String usr_username, String del_task_due_date) {
        this.app_uid = app_uid;
        this.tas_uid = tas_uid;
        this.pro_uid = pro_uid;
        this.app_pro_title = app_pro_title;
        this.app_tas_title = app_tas_title;
        this.app_create_date = app_create_date;
        this.app_update_date = app_update_date;
        this.usr_firstname = usr_firstname;
        this.usr_lastname = usr_lastname;
        this.usr_username = usr_username;
        this.del_task_due_date = del_task_due_date;
    }
}
