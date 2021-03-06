package ru.javadev;


import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import ru.javadev.auth.domain.User;
import ru.javadev.auth.ejb.UserManagerBean;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class UserLoginView implements Serializable {             //отвечает за получние данных юзера и отправки в базу
    private String login;
    private String password;
    private String city;
    private String email;
    private String number;
    User user;
    boolean loggedIn = false;
    boolean adminIn = false;
    boolean regedIn = false;

    @EJB
    UserManagerBean userManagerBean;

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isAdminIn() {
        return adminIn;
    }

    public void setAdminIn(boolean adminIn) {
        this.adminIn = adminIn;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void login() throws IOException {
        FacesMessage message = null;

        if(userManagerBean.CheckLogin(login) == 0) {       //проверка существования логина, отправка логина в EJB
            loggedIn = false;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Ошибка", "Логин не существует");
        } else if(!userManagerBean.CheckPassword(login, password)) {       //проверка совпадения пароля по логину, отправка логина и пароля в EJB
            loggedIn = false;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Ошибка", "Пароль не существует");
        } else {
            loggedIn = true;
            adminIn = userManagerBean.AdminStatus(login);     // проверка наличия прав администратора по логину, отправка логина в EJB
            user = userManagerBean.getUser(login);            //добавляем объект юзера в поле user для предоставления id другим бинам
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Добро пожаловать", login);
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
        RequestContext.getCurrentInstance().addCallbackParam("loggedIn", loggedIn);
        FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
    }

    public void registration() {
        FacesMessage message = null;

        if(userManagerBean.CheckLogin(login) != 0) {      //проверка отсутствие логина, отправка логина в EJB
            regedIn = false;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Ошибка", "Логин существует");
        } else if(userManagerBean.CheckEmail(email) != 0) {         //проверка отсутствие емейла, отправка поля email в EJB
            regedIn = false;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Ошибка", "Емайл существует");
        } else if(userManagerBean.CheckNumber(number) != 0) {           //проверка отсутствие номера
            regedIn = false;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Ошибка", "Номер телефона существует");
        } else {
            regedIn = true;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Добро пожаловать", login);
            userManagerBean.createUser(login, password, city, email, number, false);        //создание пользователя, отправка полей в EJB, установка поля admin в false
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
        RequestContext.getCurrentInstance().addCallbackParam("regedIn", regedIn);
    }

    public void exit() throws IOException {          //выход на контент незарегистрированого
        loggedIn = false;
        adminIn = false;
        RequestContext.getCurrentInstance().addCallbackParam("loggedIn", loggedIn);
        FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
    }
}