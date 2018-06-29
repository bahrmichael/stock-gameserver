package de.hackerstolz.stockgameserver.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class AnalysisItem {

    @XmlElement
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }
}
