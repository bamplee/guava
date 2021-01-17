import React from 'react'

import {Modal} from 'antd-mobile';

import classNames from 'classnames/bind';

import styles from './guavaAreaTypeFilter.scss';
import {useRecoilState, useRecoilValue} from 'recoil';
import {areaTypeState, buildingState, showAreaTypeFilterState,} from '../datatool/state';

const cx = classNames.bind(styles);

const GuavaAreaTypeFilter = () => {
    // const [building, setBuilding] = useRecoilState(buildingState);
    const [areaType, setAreaType] = useRecoilState(areaTypeState);
    const [showAreaTypeFilter, setShowAreaTypeFilter] = useRecoilState(showAreaTypeFilterState);
    const building = useRecoilValue(buildingState);

    return (
        <div className={cx('modal-container')}>
            <Modal
                popup
                // maskClosable={true}
                title={<div onClick={() => {
                    setAreaType({areaId: ''});
                    setShowAreaTypeFilter(false)
                }} className={cx('all')}>
                        전체선택
                </div>}
                visible={showAreaTypeFilter}
                onCancel={() => setShowAreaTypeFilter(false)}
                onClose={() => setShowAreaTypeFilter(false)}
                animationType="slide-up"
            >
                {
                    (building && building.areaList) &&
                    <div className={cx('area_container')}>
                        {/*<div onClick={() => {*/}
                        {/*    setAreaType({areaId: ''});*/}
                        {/*    setShowAreaTypeFilter(false)*/}
                        {/*}} className={cx('area_list')}>*/}
                        {/*    <div className={cx('all')}>*/}
                        {/*        <div>*/}
                        {/*            전체선택*/}
                        {/*        </div>*/}
                        {/*    </div>*/}
                        {/*</div>*/}
                        {
                            building && building.areaList && building.areaList.map((x, idx) =>
                                <div key={'area-list-' + idx}
                                     onClick={() => {
                                         setAreaType(x);
                                         setShowAreaTypeFilter(false)
                                     }} className={cx('area_list')}>
                                    <div className={cx('area_info')}>
                                        <div>
                                            {x.name}
                                        </div>
                                        <div>
                                            {x.hoCount}세대
                                        </div>
                                    </div>
                                    <div className={cx('area_sub_info')}>
                                        <div>
                                            전용 {Math.ceil(x.privateArea)}&#13217;
                                        </div>
                                        <div>
                                            공급 {Math.ceil(x.publicArea)}&#13217;
                                        </div>
                                    </div>
                                </div>
                            )
                        }
                    </div>
                }
            </Modal>
        </div>
    )
};

export default GuavaAreaTypeFilter;