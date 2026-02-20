package br.com.bot.cyberseguranca.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ConfigSources(
    @JsonProperty("blacklist_ips") List<String> blacklistIps,
    @JsonProperty("rss_feeds") List<RssSource> rssFeeds,
    @JsonProperty("apis") List<ApiSource> apis,
    @JsonProperty("youtube_feeds") List<YoutubeSource> youtubeFeeds,
    @JsonProperty("official_sites") List<OfficialSite> officialSites
) {}