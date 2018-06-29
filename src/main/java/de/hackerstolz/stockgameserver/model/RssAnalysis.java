package de.hackerstolz.stockgameserver.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rss")
@XmlAccessorType(XmlAccessType.FIELD)
public class RssAnalysis {

    @XmlAttribute(name="version")
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    @XmlElement(name = "channel")
    private RssChannel rssChannel;

    public RssChannel getRssChannel() {
        return rssChannel;
    }

    public void setRssChannel(final RssChannel rssChannel) {
        this.rssChannel = rssChannel;
    }
}
