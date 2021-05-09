# DB Schema and sample data
```sql
create table books
(
    id       int auto_increment
        primary key,
    title    varchar(255) not null,
    author   varchar(255) not null,
    overview varchar(255) null
)
    charset = latin1;

INSERT INTO root.books (id, title, author, overview) VALUES (1, 'Opening Spaces: An Anthology of Contemporary African Women''s Writing', 'Yvonne Vera', 'In this anthology the award-winning author Yvonne Vera brings together the stories of many talented writers from different parts of Africa.');
INSERT INTO root.books (id, title, author, overview) VALUES (2, 'The Caine Prize for African Writing 2010: 11th Annual Collection', 'The Caine Prize for African Writing', '<p>The Caine Prize for African Writing is Africa''s leading literary prize. For the past ten years it has supported and promoted contemporary African writing. Previous winners and entrants include Chimamanda Ngozi Adichie, Segun Afolabi, EC Osondu, Leila A');
INSERT INTO root.books (id, title, author, overview) VALUES (3, 'African Folktales', 'Roger D. Abrahams', null);
INSERT INTO root.books (id, title, author, overview) VALUES (4, 'Unchained Voices: An Anthology of Black Authors in the English-Speaking World of the Eighteenth Century', 'Vincent Carretta', 'Vincent Carretta has assembled the most comprehensive anthology ever published of writings by eighteenth-century people of African descent, capturing the surprisingly diverse experiences of blacks on both sides of the Atlantic--America, Britain, the West ');
INSERT INTO root.books (id, title, author, overview) VALUES (5, 'Women Writing Africa: West Africa and the Sahel', 'Esi Sutherland-Addy', '<p>The acclaimed Women Writing Africa project “opens up worlds too often excluded from the history books” (<i>Booklist</i>) and is an “essential resource for scholars and general readers alike” (<i>Library Journal</i>). It reveals the cultural legacy of A');
INSERT INTO root.books (id, title, author, overview) VALUES (6, '10 Years of the Caine Prize for African Writing: Plus Coetzee, Gordimer, Achebe, Okri', 'The Caine The Caine Prize for African Writing', null);
INSERT INTO root.books (id, title, author, overview) VALUES (7, 'Introduction to African Oral Literature and Performance', 'Bayo Ogunjimi', 'This new book puts together in a single cover, two earlier volumes by the authors, now revised to meet the challenges of a twenty-first century scholarship in African performance and cultural studies. Topics covered range from sources of African oral trad');
INSERT INTO root.books (id, title, author, overview) VALUES (8, 'Violence in Francophone African and Caribbean Women''s Literature', 'Marie-Chantal Kalisa', 'African and Caribbean peoples share a history dominated by the violent disruptions of slavery and colonialism. While much has been said about these “geographies of pain,” violence in the private sphere, particularly gendered violence, receives little atte');
INSERT INTO root.books (id, title, author, overview) VALUES (9, 'Oral Epics from Africa', 'John William Johnson', null);
INSERT INTO root.books (id, title, author, overview) VALUES (10, 'African Fundamentalism: A Literary and Cultural Anthology of Garvey''s Harlem Renaissance', 'Tony Martin', null);
```
