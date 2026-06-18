-- Fix for ORA-04043: object purchesorder does not exist
-- The PURCHASEORDER and PURCHASEORDERITEM tables don't have IDENTITY columns,
-- and any existing sequence/trigger has a typo. Create proper sequences.

-- Step 1: Create sequences (use existing max ID + 1 as START WITH to avoid conflicts)

-- For PURCHASEORDER
DECLARE
  v_max NUMBER;
BEGIN
  SELECT NVL(MAX(purchase_order_id), 0) + 1 INTO v_max FROM purchaseorder;
  EXECUTE IMMEDIATE 'CREATE SEQUENCE purchaseorder_seq START WITH ' || v_max || ' INCREMENT BY 1 NOCACHE NOCYCLE';
EXCEPTION
  WHEN OTHERS THEN
    IF SQLCODE = -955 THEN  -- ORA-00955: name is already used by an existing object
      NULL;  -- Sequence already exists, skip
    ELSE
      RAISE;
    END IF;
END;
/

-- For PURCHASEORDERITEM
DECLARE
  v_max NUMBER;
BEGIN
  SELECT NVL(MAX(purchase_order_item_id), 0) + 1 INTO v_max FROM purchaseorderitem;
  EXECUTE IMMEDIATE 'CREATE SEQUENCE purchaseorderitem_seq START WITH ' || v_max || ' INCREMENT BY 1 NOCACHE NOCYCLE';
EXCEPTION
  WHEN OTHERS THEN
    IF SQLCODE = -955 THEN
      NULL;
    ELSE
      RAISE;
    END IF;
END;
/

-- Step 2: (Optional) Drop any misspelled triggers/sequences if they exist
-- Uncomment these if you want to clean up the typos:
-- DROP TRIGGER purchesorder_trg;
-- DROP SEQUENCE purchesorder_seq;
