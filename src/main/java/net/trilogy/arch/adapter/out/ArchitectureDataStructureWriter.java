package net.trilogy.arch.adapter.out;

import net.trilogy.arch.adapter.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.tools.Csv;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArchitectureDataStructureWriter {
    private static final Log logger = LogFactory.getLog(ArchitectureDataStructureWriter.class);

    public File export(ArchitectureDataStructure dataStructure) throws IOException {
        File tempFile = File.createTempFile("arch-as-code", ".yml");

        return export(dataStructure, tempFile);
    }

    public File export(Connection connection, File writeFile) throws SQLException {
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM abcd");
        new Csv().write(writeFile.getAbsolutePath(), resultSet, "UTF-8");
        return writeFile;
    }

    public File export(ArchitectureDataStructure dataStructure, File writeFile) throws IOException {
        ArchitectureDataStructureObjectMapper mapper = new ArchitectureDataStructureObjectMapper();
        mapper.writeValue(writeFile, dataStructure);
        logger.info(String.format("Architecture data structure written to - %s", writeFile.getAbsolutePath()));
        return writeFile;
    }

}
