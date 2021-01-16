UPDATE USERS SET
                 NAME = 'Administrador',
                 LASTNAME = 'Administrador',
                 EMAIL = 'danielchungara1@gmail.com',
                 TELEFONO = '11-32652399'
WHERE ID = (SELECT ID FROM USERS WHERE USERNAME LIKE 'danielchungara1@gmail.com');
