package net.crejak.bcc;

import net.crejak.bcc.model.Model;

public abstract class AbstractController {
    protected Model model;

    public final void setModel(Model model) {
        this.model = model;
        this.onModelSet();
    }

    protected void onModelSet() {

    }
}
