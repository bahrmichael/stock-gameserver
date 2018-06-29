package de.hackerstolz.stockgameserver.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class RssChannel {

    @XmlElement
    private List<AnalysisItem> item;

    public List<AnalysisItem> getItem() {
        return item;
    }

    public void setItem(final List<AnalysisItem> item) {
        this.item = item;
    }
}
