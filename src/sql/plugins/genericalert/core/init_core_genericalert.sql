DELETE FROM core_datastore WHERE entity_key = 'genericalert.maxLength.textSms';
INSERT INTO core_datastore(entity_key, entity_value) VALUES ('genericalert.maxLength.textSms', '120');