package com.ness.movie_release_web.service.tmdb;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ness.movie_release_web.dto.tmdb.TmdbProductionCompanyDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class TmdbNetworkServiceImpl implements TmdbNetworkService {

    private List<TmdbProductionCompanyDto> networks = new ArrayList<>();

    @PostConstruct
    private void init() throws IOException {

        LocalDate date = LocalDate.now().minusDays(1);
        String url = "http://files.tmdb.org/p/exports/tv_network_ids_" + date.format(DateTimeFormatter.ofPattern("MM_dd_yyyy")) + ".json.gz";

        InputStream input = new URL(url).openStream();
        GZIPInputStream gzipInputStream = new GZIPInputStream(input);
        byte[] bytes = IOUtils.toByteArray(gzipInputStream);

        String data = new String(bytes, StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();

        data = data.replace("\n", ",");
        data = data.substring(0 , data.length() - 1);
        data = "[" + data + "]";


        List<TmdbProductionCompanyDto> result = mapper.readValue(data, new TypeReference<List<TmdbProductionCompanyDto>>() {});
        if(!result.isEmpty())
            networks = result;
    }

    @Override
    @Cacheable("getNetworks")
    public List<TmdbProductionCompanyDto> getNetworks() {
        if(networks.isEmpty()) {
            try {
                init();
            } catch (IOException e) {
                log.error(e.getLocalizedMessage());
            }
        }
        return new ArrayList<>(networks);
    }

    @Override
    @Cacheable("getNetworks")
    public List<TmdbProductionCompanyDto> getNetworks(List<Long> ids) {
        return networks.stream().filter(n -> ids.contains(n.getId())).collect(toList());
    }

    @Override
    @Cacheable("networkSearch")
    public List<TmdbProductionCompanyDto> search(String query) {
        return networks.stream()
                .filter(Objects::nonNull)
                .filter(n -> n.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(toList());
    }
}
