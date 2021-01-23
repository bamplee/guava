import React, {useEffect} from 'react'
import classNames from 'classnames/bind';

// import styles from './guavaVersusTable.scss';
import GuavaVersusCol from '../table/col/GuavaVersusCol';
import styles from '../table/guavaTable.module.scss';
import GuavaVersusRow from '../table/row/GuavaVersusRow';
import {useRecoilState} from 'recoil';
import {buildingState, regionState, showVersusSearchState, versusRegionListState} from '../datatool/state';
import GuavaBuildingVersusRow from '../table/row/GuavaBuildingVersusRow';
import {getDetail} from '../datatool/api';

const cx = classNames.bind(styles);

const GuavaVersusTable = () => {
    const [region, setRegion] = useRecoilState(regionState);
    const [building, setBuilding] = useRecoilState(buildingState);
    const [versusRegionList, setVersusRegionList] = useRecoilState(versusRegionListState);

    useEffect(() => {
        const init = async () => {
            if(region && region.buildingId) {
                setBuilding(await getDetail(region.buildingId));
            }
        };
        init();
    }, []);

    return (
        <div className={cx('table_container')}>
            <div className={cx('list')}>
                <GuavaVersusCol/>
                <div className={cx('body')}>
                    <GuavaBuildingVersusRow page={0} idx={0} building={building}/>
                    {
                        versusRegionList.map((building, idx) => <GuavaBuildingVersusRow page={0} idx={idx} building={building}/>)
                    }
                </div>
            </div>
        </div>
    )
};


export default GuavaVersusTable;