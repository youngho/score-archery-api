-- Unity 씬 이름(스테이지 식별·표시용)을 플레이 기록에 보관
ALTER TABLE match_history
    ADD COLUMN stage_scene_name VARCHAR(64) NULL
    AFTER stage_number;
