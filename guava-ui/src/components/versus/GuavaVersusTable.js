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

const GuavaVersusTable = ({versusRegionList, setVersusRegionList}) => {
    const [region, setRegion] = useRecoilState(regionState);
    // const [versusRegionList, setVersusRegionList] = useRecoilState(versusRegionListState);

    const removeVersusRegion = (region) => {
        let temp = [...versusRegionList];
        temp = temp.filter(x => !(x.type === 'BUILDING' ? x.buildingId === region.buildingId : x.id === region.id));
        setVersusRegionList([...temp]);
    };

    return (
        <div className={cx('table_container')}>
            <div className={cx('list')}>
                <GuavaVersusCol/>
                <div className={cx('body')}>
                    {/*<GuavaBuildingVersusRow page={0} idx={0} building={building}/>*/}
                    {
                        versusRegionList.map((building, idx) => <GuavaBuildingVersusRow page={0} idx={idx}
                                                                                        building={building}
                                                                                        handleClick={removeVersusRegion}/>)
                    }
                    {
                        versusRegionList.length > 0 &&
                        <div className={cx('empty_result')}>총 검색결과 {versusRegionList.length}개</div>
                    }
                </div>
            </div>
        </div>
    )
};


export default GuavaVersusTable;