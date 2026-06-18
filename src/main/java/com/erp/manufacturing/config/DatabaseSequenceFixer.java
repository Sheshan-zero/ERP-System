package com.erp.manufacturing.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSequenceFixer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSequenceFixer.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSequenceFixer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Checking and repairing database sequences...");
        try {
            // Fix sequence for ITEM
            alignSequence("item_seq", "item", "item_id");
            
            // Also align other sequences if they exist and are out of sync
            alignSequence("purchaseorder_seq", "purchaseorder", "purchase_order_id");
            alignSequence("purchaseorderitem_seq", "purchaseorderitem", "purchase_order_item_id");

            log.info("Database sequences checked and repaired successfully.");
        } catch (Exception e) {
            log.error("Failed to repair database sequences on startup", e);
        }
    }

    private void alignSequence(String sequenceName, String tableName, String idColumnName) {
        try {
            // Get maximum ID from the table
            String maxIdSql = "SELECT NVL(MAX(" + idColumnName + "), 0) FROM " + tableName;
            Long maxId = jdbcTemplate.queryForObject(maxIdSql, Long.class);
            if (maxId == null) {
                maxId = 0L;
            }
            long startWith = maxId + 1;

            log.info("Aligning sequence '{}' for table '{}' (ID column '{}'). Max ID: {}, Next Value: {}",
                    sequenceName, tableName, idColumnName, maxId, startWith);

            // Drop existing sequence
            try {
                jdbcTemplate.execute("DROP SEQUENCE " + sequenceName);
                log.debug("Dropped sequence '{}'", sequenceName);
            } catch (Exception e) {
                // Ignore if sequence doesn't exist
                log.debug("Sequence '{}' did not exist or could not be dropped: {}", sequenceName, e.getMessage());
            }

            // Create sequence with correct START WITH
            String createSeqSql = "CREATE SEQUENCE " + sequenceName + 
                    " START WITH " + startWith + 
                    " INCREMENT BY 1 NOCACHE NOCYCLE";
            jdbcTemplate.execute(createSeqSql);
            log.info("Successfully recreated sequence '{}' starting with {}", sequenceName, startWith);

        } catch (Exception e) {
            log.warn("Could not align sequence '{}' for table '{}': {}", sequenceName, tableName, e.getMessage());
        }
    }
}
