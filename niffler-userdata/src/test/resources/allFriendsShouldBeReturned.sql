INSERT INTO public."user" (id, username, currency, firstname, surname, photo, photo_small, full_name)
VALUES ('22ac2be8-3610-425e-a00c-b86c56dff8a7', 'ivan', 'RUB', null, null, null, null,null);

INSERT INTO public."user" (id, username, currency, firstname, surname, photo, photo_small, full_name)
VALUES ('d2814723-22d5-47ed-a563-2fde03cc573b', 'ivan1', 'RUB', null, null, null, null,null);

INSERT INTO public."user" (id, username, currency, firstname, surname, photo, photo_small, full_name)
VALUES ('1beeb12e-372a-4fd9-b878-772edf3c429e', 'ivan2', 'RUB', null, null, null, null,null);

INSERT INTO public.friendship (requester_id, addressee_id, status, created_date)
VALUES ('22ac2be8-3610-425e-a00c-b86c56dff8a7', 'd2814723-22d5-47ed-a563-2fde03cc573b', 'ACCEPTED', CURRENT_DATE);

INSERT INTO public.friendship (requester_id, addressee_id, status, created_date)
VALUES ('22ac2be8-3610-425e-a00c-b86c56dff8a7', '1beeb12e-372a-4fd9-b878-772edf3c429e', 'ACCEPTED', CURRENT_DATE);

INSERT INTO public.friendship (requester_id, addressee_id, status, created_date)
VALUES ('d2814723-22d5-47ed-a563-2fde03cc573b', '22ac2be8-3610-425e-a00c-b86c56dff8a7', 'ACCEPTED', CURRENT_DATE);

INSERT INTO public.friendship (requester_id, addressee_id, status, created_date)
VALUES ('1beeb12e-372a-4fd9-b878-772edf3c429e', '22ac2be8-3610-425e-a00c-b86c56dff8a7', 'ACCEPTED', CURRENT_DATE);