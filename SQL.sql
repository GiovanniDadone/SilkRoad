USE Silkroad;

SELECT *
FROM products;

UPDATE utenti set RUOLO = "ADMIN" WHERE ID = 2;




INSERT INTO categories (name, description, display_order, image_url, is_active, parent_category_id) VALUES
('Crypto Marketplaces', 'Piattaforme per lo scambio di criptovalute e asset digitali', 1, 'http://example.com/images/crypto_market.jpg', 1, 1),
('Cybersecurity Tools', 'Strumenti per sicurezza, anonimato e protezione dei dati', 2, 'http://example.com/images/cybersec_tools.jpg', 1, 2),
('Hacking Resources', 'Manuali, eBook e software per ethical hacking', 3, 'http://example.com/images/hacking_resources.jpg', 1, 3),
('Anonymous Communication', 'App e servizi per chat anonime e cifrate', 4, 'http://example.com/images/anon_chat.jpg', 1, 4),
('Darknet Forums', 'Forum e community della darknet su vari argomenti', 5, 'http://example.com/images/darknet_forums.jpg', 1, 5),
('VPN & Proxy Services', 'VPN, proxy e strumenti per la privacy online', 6, 'http://example.com/images/vpn_proxy.jpg', 1, 6),
('Whistleblower Platforms', 'Servizi per segnalazioni anonime e leak', 7, 'http://example.com/images/whistleblow.jpg', 1, 7),
('Darknet Market Analysis', 'Report e analisi dei mercati darknet (legali)', 8, 'http://example.com/images/market_analysis.jpg', 1, 8),
('Privacy Hardware', 'Dispositivi fisici per la sicurezza e l’anonimato', 9, 'http://example.com/images/privacy_hardware.jpg', 1, 9),
('OSINT Tools', 'Strumenti Open Source Intelligence per investigazione', 10, 'http://example.com/images/osint.jpg', 1, 10);



INSERT INTO products (name, description, image_url, is_active, price, sku, stock_quantity, category_id) VALUES

-- Crypto Marketplaces
('Ledger Nano S Plus', 'Wallet hardware per criptovalute con elevati standard di sicurezza.', 'http://example.com/images/ledger_nano.jpg', 1, 79.99, 'CRYPTO-LEDGER-001', 40, (SELECT id FROM categories WHERE name = 'Crypto Marketplaces')),
('Opendime Stick', 'Chiavetta USB per bitcoin con funzionalità “bearer”, sicurezza avanzata.', 'http://example.com/images/opendime.jpg', 1, 59.00, 'CRYPTO-OPENDIME-002', 60, (SELECT id FROM categories WHERE name = 'Crypto Marketplaces')),

-- Cybersecurity Tools
('YubiKey 5 NFC', 'Security key hardware compatibile FIDO2/U2F, supporta autenticazione a due fattori.', 'http://example.com/images/yubikey5nfc.jpg', 1, 49.00, 'CYBER-YUBI-003', 120, (SELECT id FROM categories WHERE name = 'Cybersecurity Tools')),
('Bitdefender Total Security', 'Suite completa di sicurezza informatica multi-piattaforma (1 anno).', 'http://example.com/images/bitdefender.jpg', 1, 39.99, 'CYBER-BITDEF-004', 80, (SELECT id FROM categories WHERE name = 'Cybersecurity Tools')),

-- Hacking Resources
('Practical Ethical Hacking (eBook)', 'Guida moderna all’hacking etico, penetration testing e CTF.', 'http://example.com/images/ethical_hacking_ebook.jpg', 1, 29.00, 'HACK-PEH-EBOOK', 200, (SELECT id FROM categories WHERE name = 'Hacking Resources')),
('WiFi Hacking Lab Kit', 'Kit di hardware (antenne, chiavetta WiFi compatibile) per laboratori di penetration test wireless.', 'http://example.com/images/wifi_hacking_kit.jpg', 1, 75.00, 'HACK-WIFI-KIT', 30, (SELECT id FROM categories WHERE name = 'Hacking Resources')),

-- Anonymous Communication
('ProtonMail Plus (1 anno)', 'Casella email anonima e cifrata con server in Svizzera.', 'http://example.com/images/protonmail.jpg', 1, 49.00, 'COMM-PROTON-PLUS', 150, (SELECT id FROM categories WHERE name = 'Anonymous Communication')),
('Briar Messenger (App)', 'App open-source per chat cifrate e peer-to-peer senza server centrale.', 'http://example.com/images/briar_messenger.jpg', 1, 0.00, 'COMM-BRIAR-APP', 500, (SELECT id FROM categories WHERE name = 'Anonymous Communication')),

-- Darknet Forums
('Darknet & Privacy Forum Access', 'Abbonamento a forum privati di discussione su privacy, sicurezza, cyber.', 'http://example.com/images/darknet_forum.jpg', 1, 10.00, 'FORUM-ACCESS-001', 100, (SELECT id FROM categories WHERE name = 'Darknet Forums')),

-- VPN & Proxy Services
('NordVPN 1 anno', 'Abbonamento VPN con oltre 5000 server in 60 paesi, nessun log.', 'http://example.com/images/nordvpn.jpg', 1, 39.00, 'VPN-NORD-001', 400, (SELECT id FROM categories WHERE name = 'VPN & Proxy Services')),
('Mullvad VPN 6 mesi', 'VPN svedese che non richiede dati personali, massima privacy.', 'http://example.com/images/mullvad.jpg', 1, 25.00, 'VPN-MULL-002', 250, (SELECT id FROM categories WHERE name = 'VPN & Proxy Services')),

-- Whistleblower Platforms
('SecureDrop Hosting Guide', 'Manuale tecnico per implementare SecureDrop per whistleblowing.', 'http://example.com/images/securedrop.jpg', 1, 15.00, 'WHISTLE-SD-GUIDE', 90, (SELECT id FROM categories WHERE name = 'Whistleblower Platforms')),

-- Darknet Market Analysis
('Darknet Markets Report 2025', 'Report analitico annuale sui marketplace darknet (solo dati e statistiche, niente market access).', 'http://example.com/images/darknet_report.jpg', 1, 55.00, 'ANALYSIS-REPORT-2025', 60, (SELECT id FROM categories WHERE name = 'Darknet Market Analysis')),

-- Privacy Hardware
('Librem 5 Phone', 'Smartphone open-source e privacy-focused con interruttori hardware per camera, microfono e connessioni.', 'http://example.com/images/librem5.jpg', 1, 849.00, 'PRIV-HW-LIBREM5', 12, (SELECT id FROM categories WHERE name = 'Privacy Hardware')),
('Tails USB Ready', 'Chiavetta USB con sistema operativo Tails preinstallato, pronto all’uso per navigazione anonima.', 'http://example.com/images/tails_usb.jpg', 1, 39.99, 'PRIV-HW-TAILSUSB', 200, (SELECT id FROM categories WHERE name = 'Privacy Hardware')),

-- OSINT Tools
('Maltego Pro (licenza 1 anno)', 'Strumento professionale di OSINT per investigazione e analisi dati.', 'http://example.com/images/maltego.jpg', 1, 899.00, 'OSINT-MALTEGO', 15, (SELECT id FROM categories WHERE name = 'OSINT Tools')),
('OSINT Starter Kit', 'Raccolta di software, VM e manuali per attività OSINT e ricerca info open source.', 'http://example.com/images/osint_kit.jpg', 1, 120.00, 'OSINT-STARTER-KIT', 35, (SELECT id FROM categories WHERE name = 'OSINT Tools'))
;


SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM categories WHERE id = 1;

SET FOREIGN_KEY_CHECKS = 1;

DROP DATABASE silkroad;

SELECT *
FROM user_roles;


INSERT INTO ruoli (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');


ALTER TABLE utenti DROP COLUMN ruolo;

SELECT *
FROM utenti;

