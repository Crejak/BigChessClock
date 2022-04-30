package net.crejak.bcc.controllers;

import net.crejak.bcc.model.Model;

public abstract class AbstractController {
    protected Model model;

    public AbstractController() {
        this.model = Model.getInstance();
    }
}
